package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.model.CompanyProfileDTO;
import com.main.nexus_frontend.model.ProfessionalProfileDTO;
import com.main.nexus_frontend.model.ReviewDisplayDTO;
import com.main.nexus_frontend.model.ReviewPageDTO;
import com.main.nexus_frontend.service.CompanyService;
import com.main.nexus_frontend.service.ProfessionalService;
import com.main.nexus_frontend.service.PublicService;
import com.main.nexus_frontend.service.ReviewBffService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

// Páginas dedicadas de avaliações (profissional/empresa) e os endpoints JSON usados
// pelo filtro de estrelas dessas páginas e pelo card de preview nos perfis. Tudo público
// — avaliação é informação de perfil, visível por qualquer visitante.
@Controller
public class ReviewBffController {

    @Autowired
    private ReviewBffService reviewBffService;

    @Autowired
    private PublicService publicService;

    @Autowired
    private ProfessionalService professionalService;

    @Autowired
    private CompanyService companyService;

    // Páginas dedicadas — mesmo namespace /public/... dos perfis (public-profile.html,
    // public-company.html) que essas páginas complementam.

    @GetMapping("/public/professional/{professionalId}/reviews")
    public String professionalReviews(@PathVariable Long professionalId, Model model) {
        ReviewPageDTO reviews;
        String professionalName;
        try {
            reviews = reviewBffService.getAllForProfessional(professionalId, null);
            professionalName = publicService.getProfessional(professionalId).getName();
        } catch (Exception e) {
            return "redirect:/?error=Profissional+nao+encontrado";
        }

        model.addAttribute("reviews", reviews);
        model.addAttribute("profileType", "professional");
        model.addAttribute("profileId", professionalId);
        model.addAttribute("profileName", professionalName);
        model.addAttribute("ratingFilter", (Integer) null);
        return "public/professional-reviews";
    }

    @GetMapping("/public/company/{companyId}/reviews")
    public String companyReviews(@PathVariable Long companyId, Model model) {
        ReviewPageDTO reviews;
        String companyName;
        try {
            reviews = reviewBffService.getAllForCompany(companyId, null);
            companyName = publicService.getCompany(companyId).getCompanyName();
        } catch (Exception e) {
            return "redirect:/?error=Empresa+nao+encontrada";
        }

        model.addAttribute("reviews", reviews);
        model.addAttribute("profileType", "company");
        model.addAttribute("profileId", companyId);
        model.addAttribute("profileName", companyName);
        model.addAttribute("ratingFilter", (Integer) null);
        return "public/company-reviews";
    }

    // "Minhas avaliações" — item "Avaliações" da sidebar, mesmo padrão auto-referente de
    // /pro/profile e /company/profile (resolve o dono a partir do token da sessão, sem ID
    // na URL). Reaproveita os mesmos templates das páginas públicas, com activePage
    // marcado pra destacar o item na sidebar — só faz sentido quando é o próprio dono
    // olhando as próprias avaliações, então as rotas /public/.../reviews (visão de
    // terceiros) continuam sem setar activePage.

    @GetMapping("/pro/reviews")
    public String myProfessionalReviews(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        try {
            ProfessionalProfileDTO profile = professionalService.getProfile(token);
            ReviewPageDTO reviews = reviewBffService.getAllForProfessional(profile.getId(), null);

            model.addAttribute("reviews", reviews);
            model.addAttribute("profileType", "professional");
            model.addAttribute("profileId", profile.getId());
            model.addAttribute("profileName", profile.getName());
            model.addAttribute("ratingFilter", (Integer) null);
            model.addAttribute("activePage", "reviews");
            return "public/professional-reviews";
        } catch (Exception e) {
            return "redirect:/pro/profile";
        }
    }

    @GetMapping("/company/reviews")
    public String myCompanyReviews(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        try {
            CompanyProfileDTO profile = companyService.getProfile(token);
            ReviewPageDTO reviews = reviewBffService.getAllForCompany(profile.getId(), null);

            model.addAttribute("reviews", reviews);
            model.addAttribute("profileType", "company");
            model.addAttribute("profileId", profile.getId());
            model.addAttribute("profileName", profile.getCompanyName());
            model.addAttribute("ratingFilter", (Integer) null);
            model.addAttribute("activePage", "reviews");
            return "public/company-reviews";
        } catch (Exception e) {
            return "redirect:/company/profile";
        }
    }

    // Contagem pro badge do item "Avaliações" da sidebar — roda em toda página de quem
    // está logado (igual /app-api/chat/unread-total), então resolve o dono pela role da
    // sessão em vez de exigir um ID na URL.
    @GetMapping("/app-api/reviews/my-count")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> myReviewCount(HttpSession session) {
        String token = (String) session.getAttribute("token");
        String role = (String) session.getAttribute("userRole");
        if (token == null || role == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            long total;
            if ("PROFESSIONAL".equals(role)) {
                total = reviewBffService.getCountForProfessional(professionalService.getProfile(token).getId());
            } else if ("COMPANY".equals(role)) {
                total = reviewBffService.getCountForCompany(companyService.getProfile(token).getId());
            } else {
                total = 0;
            }
            return ResponseEntity.ok(Map.of("totalReviews", total));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("totalReviews", 0L));
        }
    }

    // Endpoints JSON — chamados via fetch pelo card de preview (top3, em todo perfil)
    // e pelo filtro de estrelas da página dedicada (all, com re-render sem reload).

    @GetMapping("/app-api/reviews/professional/{professionalId}/top3")
    @ResponseBody
    public ResponseEntity<List<ReviewDisplayDTO>> top3Professional(@PathVariable Long professionalId) {
        try {
            return ResponseEntity.ok(reviewBffService.getTop3ForProfessional(professionalId));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @GetMapping("/app-api/reviews/company/{companyId}/top3")
    @ResponseBody
    public ResponseEntity<List<ReviewDisplayDTO>> top3Company(@PathVariable Long companyId) {
        try {
            return ResponseEntity.ok(reviewBffService.getTop3ForCompany(companyId));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @GetMapping("/app-api/reviews/professional/{professionalId}/all")
    @ResponseBody
    public ResponseEntity<ReviewPageDTO> allProfessional(
            @PathVariable Long professionalId,
            @RequestParam(required = false) Integer rating) {
        try {
            return ResponseEntity.ok(reviewBffService.getAllForProfessional(professionalId, rating));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @GetMapping("/app-api/reviews/company/{companyId}/all")
    @ResponseBody
    public ResponseEntity<ReviewPageDTO> allCompany(
            @PathVariable Long companyId,
            @RequestParam(required = false) Integer rating) {
        try {
            return ResponseEntity.ok(reviewBffService.getAllForCompany(companyId, rating));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }
}
