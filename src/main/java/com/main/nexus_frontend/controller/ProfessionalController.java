package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.exception.NexusApiException;
import com.main.nexus_frontend.model.*;
import com.main.nexus_frontend.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pro")
public class ProfessionalController {

    // Mais recentes primeiro — mesmo critério usado nas abas de matches do lado da empresa.
    private static final Comparator<MatchDTO> BY_CREATED_AT_DESC =
            Comparator.comparing(MatchDTO::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed();

    @Autowired
    private ProfessionalService professionalService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private MatchService matchService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReputationService reputationService;
    @Autowired
    private MapService mapService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        String userName = (String) session.getAttribute("userName");

        List<MatchDTO> allMatches = professionalService.getMatches(token);
        List<MatchDTO> invites = professionalService.getPendingInvites(token);
        List<PreviousProjectDTO> projects = professionalService.getProjects(token);

        List<MatchDTO> confirmed = allMatches.stream()
                .filter(m -> "MATCHED".equals(m.getStatus()))
                .collect(Collectors.toList());

        ProfessionalStatsDTO stats;
        try {
            stats = professionalService.getStats(token);
        } catch (Exception e) {
            stats = new ProfessionalStatsDTO();
        }

        // Usado pelo card de alerta "conta indisponível" no topo do dashboard.
        boolean isAvailable = true;
        try {
            Boolean available = professionalService.getProfile(token).getAvailable();
            isAvailable = available == null || available;
        } catch (Exception e) {
            // Mantém o padrão (disponível) se não conseguir carregar o perfil
        }

        model.addAttribute("userName", userName);
        model.addAttribute("isAvailable", isAvailable);
        model.addAttribute("invites", invites);
        model.addAttribute("invitesCount", invites.size());
        model.addAttribute("confirmed", confirmed);
        model.addAttribute("confirmedCount", confirmed.size());
        model.addAttribute("stats", stats);
        model.addAttribute("recentInvites", invites.stream().limit(5).collect(Collectors.toList()));
        model.addAttribute("recentConfirmed", confirmed.stream().limit(5).collect(Collectors.toList()));
        model.addAttribute("portfolioCount", projects.size());
        // Se tiver um match expirado (30 dias) ainda sem avaliação, a pergunta abre
        // sozinha no dashboard — o profissional não precisa ir procurar na notificação.
        model.addAttribute("pendingReview", reviewService.getPendingForProfessional(token));
        model.addAttribute("activePage", "dashboard");

        return "pro/pro-dashboard";
    }

    @GetMapping("/profile")
    public String profile(
            @RequestParam(required = false) String linkedinLinked,
            @RequestParam(required = false) String linkedinError,
            @RequestParam(required = false) String githubLinked,
            @RequestParam(required = false) String githubError,
            HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        ProfessionalProfileDTO profile = professionalService.getProfile(token);
        List<SkillDTO> allSkills = professionalService.getAllSkills(token);

        int projectsCount;
        try {
            projectsCount = professionalService.getProjects(token).size();
        } catch (Exception e) {
            projectsCount = 0;
        }

        // ProfessionalProfileDTO.skills só tem os nomes (não os ids) — o componente de
        // seleção de skills precisa dos ids pra pré-marcar, então resolve cruzando com
        // o catálogo completo já carregado acima.
        List<Long> currentSkillIds = allSkills.stream()
                .filter(sk -> profile.getSkills() != null && profile.getSkills().contains(sk.getName()))
                .map(SkillDTO::getId)
                .toList();

        model.addAttribute("profile", profile);
        model.addAttribute("allSkills", allSkills);
        model.addAttribute("currentSkillIds", currentSkillIds);
        model.addAttribute("projectsCount", projectsCount);
        try {
            model.addAttribute("reputation", reputationService.getProfessional(token, profile.getId()));
        } catch (Exception e) {
            model.addAttribute("reputation", new ReputationDTO());
        }
        if ("true".equals(linkedinLinked)) {
            boolean hasLinkedinUrl = profile.getLinkedinUrl() != null && !profile.getLinkedinUrl().isBlank();
            model.addAttribute("successMsg", hasLinkedinUrl
                    ? "Conta do LinkedIn conectada e link do perfil salvo com sucesso!"
                    : "Conta do LinkedIn conectada! Agora informe o link do seu perfil abaixo, em \"Editar\".");
        }
        if ("already_linked".equals(linkedinError)) {
            model.addAttribute("errorMsg", "Esta conta do LinkedIn já está vinculada a outro usuário do Nexus.");
        }
        if ("true".equals(githubLinked)) {
            model.addAttribute("successMsg", "Conta do GitHub conectada com sucesso!");
        }
        if ("already_linked".equals(githubError)) {
            model.addAttribute("errorMsg", "Esta conta do GitHub já está vinculada a outro profissional do Nexus.");
        }
        model.addAttribute("activePage", "profile");
        return "pro/pro-profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String cep,
            @RequestParam(value = "preferredTypes", required = false) List<String> preferredTypes,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(value = "preferredOpportunityTypes", required = false) List<String> preferredOpportunityTypes,
            @RequestParam(required = false) Double expectedSalaryCLT,
            @RequestParam(required = false) Double expectedSalaryPJ,
            @RequestParam(required = false) Double freelanceMinExpectation,
            @RequestParam(required = false) Double freelanceMaxExpectation,
            @RequestParam(required = false) String linkedinUrl,
            @RequestParam(required = false) String githubUrl,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            // available não faz parte deste form (tem endpoint próprio) — preserva o valor atual
            ProfessionalProfileDTO currentProfile = professionalService.getProfile(token);

            ProfessionalProfileDTO dto = new ProfessionalProfileDTO();
            dto.setName(name);
            dto.setPhone(phone);
            dto.setCep(cep);
            dto.setAvailable(currentProfile.getAvailable());
            dto.setPreferredTypes(preferredTypes);
            dto.setExperienceLevel(experienceLevel != null && !experienceLevel.isBlank() ? experienceLevel : null);
            dto.setPreferredOpportunityTypes(preferredOpportunityTypes);
            dto.setLinkedinUrl(linkedinUrl != null && !linkedinUrl.isBlank() ? linkedinUrl : null);
            dto.setGithubUrl(githubUrl != null && !githubUrl.isBlank() ? githubUrl : null);
            dto.setExpectedSalaryCLT(expectedSalaryCLT);
            dto.setExpectedSalaryPJ(expectedSalaryPJ);
            dto.setFreelanceMinExpectation(freelanceMinExpectation);
            dto.setFreelanceMaxExpectation(freelanceMaxExpectation);
            professionalService.updateProfile(token, dto);
            redirectAttributes.addFlashAttribute("successMsg", "Perfil atualizado com sucesso!");
        } catch (NexusApiException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao atualizar perfil: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Não foi possível conectar ao servidor. Tente novamente em instantes.");
        }
        return "redirect:/pro/profile";
    }

    @PostMapping("/profile/linkedin")
    @ResponseBody
    public ResponseEntity<String> saveLinkedinUrl(
            @RequestParam String linkedinUrl,
            HttpSession session) {
        String token = (String) session.getAttribute("token");
        try {
            ProfessionalProfileDTO currentProfile = professionalService.getProfile(token);
            currentProfile.setLinkedinUrl(linkedinUrl != null && !linkedinUrl.isBlank() ? linkedinUrl : null);
            professionalService.updateProfile(token, currentProfile);
            return ResponseEntity.ok("LinkedIn salvo.");
        } catch (NexusApiException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Não foi possível salvar o link do LinkedIn.");
        }
    }

    @PostMapping("/profile/skills")
    public String updateSkills(
            @RequestParam(value = "skillIds", required = false) List<Long> skillIds,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            professionalService.updateSkills(token, skillIds != null ? skillIds : List.of());
            redirectAttributes.addFlashAttribute("successMsg", "Skills atualizadas com sucesso!");
        } catch (NexusApiException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao atualizar skills: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Não foi possível conectar ao servidor. Tente novamente em instantes.");
        }
        return "redirect:/pro/profile";
    }

    @PostMapping("/profile/availability")
    public String updateAvailability(
            @RequestParam(value = "available", required = false) String available,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            ProfessionalProfileDTO currentProfile = professionalService.getProfile(token);
            ProfessionalProfileDTO dto = new ProfessionalProfileDTO();
            dto.setName(currentProfile.getName());
            dto.setPhone(currentProfile.getPhone());
            dto.setCep(currentProfile.getCep());
            dto.setPreferredTypes(currentProfile.getPreferredTypes());
            dto.setExperienceLevel(currentProfile.getExperienceLevel());
            dto.setPreferredOpportunityTypes(currentProfile.getPreferredOpportunityTypes());
            dto.setExpectedSalaryCLT(currentProfile.getExpectedSalaryCLT());
            dto.setExpectedSalaryPJ(currentProfile.getExpectedSalaryPJ());
            dto.setFreelanceMinExpectation(currentProfile.getFreelanceMinExpectation());
            dto.setFreelanceMaxExpectation(currentProfile.getFreelanceMaxExpectation());
            dto.setLinkedinUrl(currentProfile.getLinkedinUrl());
            dto.setGithubUrl(currentProfile.getGithubUrl());
            dto.setAvailable("on".equals(available));
            professionalService.updateProfile(token, dto);
            redirectAttributes.addFlashAttribute("successMsg", "Disponibilidade atualizada!");
        } catch (NexusApiException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao atualizar disponibilidade: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Não foi possível conectar ao servidor. Tente novamente em instantes.");
        }
        return "redirect:/pro/profile";
    }

    @PostMapping("/credentials")
    public String addCredential(
            @RequestParam String type,
            @RequestParam String name,
            @RequestParam String color,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        boolean isEvent = "EVENT".equals(type);
        try {
            ProfessionalCredentialDTO dto = new ProfessionalCredentialDTO();
            dto.setType(type);
            dto.setName(name);
            dto.setColor(color);
            professionalService.addCredential(token, dto);
            redirectAttributes.addFlashAttribute("successMsg",
                    isEvent ? "Evento adicionado com sucesso!" : "Certificado adicionado com sucesso!");
        } catch (NexusApiException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao adicionar: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Não foi possível conectar ao servidor. Tente novamente em instantes.");
        }
        return "redirect:/pro/profile";
    }

    @PostMapping("/credentials/{credentialId}/edit")
    public String editCredential(
            @PathVariable Long credentialId,
            @RequestParam String type,
            @RequestParam String name,
            @RequestParam String color,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            ProfessionalCredentialDTO dto = new ProfessionalCredentialDTO();
            dto.setType(type);
            dto.setName(name);
            dto.setColor(color);
            professionalService.updateCredential(token, credentialId, dto);
            redirectAttributes.addFlashAttribute("successMsg", "Atualizado com sucesso!");
        } catch (NexusApiException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao atualizar: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Não foi possível conectar ao servidor. Tente novamente em instantes.");
        }
        return "redirect:/pro/profile";
    }

    @PostMapping("/credentials/{credentialId}/delete")
    public String deleteCredential(
            @PathVariable Long credentialId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            professionalService.deleteCredential(token, credentialId);
            redirectAttributes.addFlashAttribute("successMsg", "Removido com sucesso!");
        } catch (NexusApiException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao remover: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Não foi possível conectar ao servidor. Tente novamente em instantes.");
        }
        return "redirect:/pro/profile";
    }

    @GetMapping("/portfolio")
    public String portfolio(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        List<PreviousProjectDTO> projects = professionalService.getProjects(token);
        model.addAttribute("projects", projects);
        model.addAttribute("activePage", "portfolio");
        return "pro/pro-portfolio";
    }

    @PostMapping("/portfolio")
    public String addProject(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<String> technologies,
            @RequestParam Integer yearOfCompletion,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            PreviousProjectDTO dto = new PreviousProjectDTO();
            dto.setTitle(title);
            dto.setDescription(description);
            dto.setTechnologies(technologies != null ? technologies : List.of());
            dto.setYearOfCompletion(yearOfCompletion);
            professionalService.addProject(token, dto);
            redirectAttributes.addFlashAttribute("successMsg", "Projeto adicionado com sucesso!");
        } catch (NexusApiException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao adicionar projeto: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Não foi possível conectar ao servidor. Tente novamente em instantes.");
        }
        return "redirect:/pro/portfolio";
    }

    @PostMapping("/portfolio/{projectId}/edit")
    public String editProject(
            @PathVariable Long projectId,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<String> technologies,
            @RequestParam Integer yearOfCompletion,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            PreviousProjectDTO dto = new PreviousProjectDTO();
            dto.setTitle(title);
            dto.setDescription(description);
            dto.setTechnologies(technologies != null ? technologies : List.of());
            dto.setYearOfCompletion(yearOfCompletion);
            professionalService.updateProject(token, projectId, dto);
            redirectAttributes.addFlashAttribute("successMsg", "Projeto atualizado com sucesso!");
        } catch (NexusApiException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao atualizar projeto: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Não foi possível conectar ao servidor. Tente novamente em instantes.");
        }
        return "redirect:/pro/portfolio";
    }

    @PostMapping("/portfolio/{projectId}/delete")
    public String deleteProject(
            @PathVariable Long projectId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            professionalService.deleteProject(token, projectId);
            redirectAttributes.addFlashAttribute("successMsg", "Projeto removido com sucesso!");
        } catch (NexusApiException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao remover projeto: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Não foi possível conectar ao servidor. Tente novamente em instantes.");
        }
        return "redirect:/pro/portfolio";
    }

    // Skills do próprio profissional logado — usado só pra colorir os chips de "Skills
    // exigidas" nas telas de oportunidades/matches (azul quando ele já tem a skill, vermelho
    // claro quando não tem). Nunca quebra a página se o perfil não puder ser carregado.
    private List<String> getMySkillNames(String token) {
        try {
            List<String> skills = professionalService.getProfile(token).getSkills();
            return skills != null ? skills : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    @GetMapping("/matches")
    public String matches(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        model.addAttribute("mySkills", getMySkillNames(token));
        List<MatchDTO> allMatches = professionalService.getMatches(token);
        List<MatchDTO> invites = professionalService.getPendingInvites(token);
        List<MatchDTO> sent = professionalService.getSentInterests(token);
        List<MatchDTO> confirmed = allMatches.stream()
                .filter(m -> "MATCHED".equals(m.getStatus()))
                .collect(Collectors.toList());
        List<MatchDTO> previousProjects = professionalService.getPreviousMatches(token);
        List<MatchDTO> rejected = allMatches.stream()
                .filter(m -> "REJECTED".equals(m.getStatus()))
                .collect(Collectors.toList());

        invites.sort(BY_CREATED_AT_DESC);
        sent.sort(BY_CREATED_AT_DESC);
        confirmed.sort(BY_CREATED_AT_DESC);
        previousProjects.sort(BY_CREATED_AT_DESC);
        rejected.sort(BY_CREATED_AT_DESC);

        model.addAttribute("invites", invites);
        model.addAttribute("sent", sent);
        model.addAttribute("confirmed", confirmed);
        model.addAttribute("previousProjects", previousProjects);
        model.addAttribute("rejected", rejected);
        model.addAttribute("reviewedMatchIds", reviewService.getReviewedMatchIdsForProfessional(token));
        model.addAttribute("activePage", "matches");
        return "pro/pro-matches";
    }

    // Perfil da empresa com dados de contato — só liberado após match confirmado
    @GetMapping("/company/{id}")
    public String viewCompany(@PathVariable Long id, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        CompanyDTO company = companyService.getPublicProfile(id);
        model.addAttribute("company", company);
        model.addAttribute("closedProjects", companyService.getCompanyClosedProjects(id));

        ContactInfoDTO contact = null;
        try {
            contact = companyService.getContact(token, id);
        } catch (Exception e) {
            // Sem match confirmado — contato permanece oculto
        }
        model.addAttribute("contact", contact);

        model.addAttribute("activePage", "matches");
        return "pro/pro-company-view";
    }

    @PostMapping("/matches/{matchId}/accept")
    public String acceptMatch(
            @PathVariable Long matchId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            matchService.professionalAccept(token, matchId);
            redirectAttributes.addFlashAttribute("successMsg", "Convite aceito! Match confirmado.");
            redirectAttributes.addFlashAttribute("justConfirmedMatchId", matchId);
        } catch (NexusApiException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao aceitar convite: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Não foi possível conectar ao servidor. Tente novamente em instantes.");
        }
        return "redirect:/pro/matches";
    }

    @PostMapping("/matches/{matchId}/reject")
    public String rejectMatch(
            @PathVariable Long matchId,
            @RequestParam(value = "reasons", required = false) List<String> reasons,
            @RequestParam(value = "description", required = false) String description,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            matchService.professionalReject(token, matchId, reasons != null ? reasons : List.of(), description);
            redirectAttributes.addFlashAttribute("successMsg", "Convite recusado.");
        } catch (NexusApiException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao recusar convite: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Não foi possível conectar ao servidor. Tente novamente em instantes.");
        }
        return "redirect:/pro/matches";
    }

    @PostMapping("/matches/{matchId}/cancel")
    public String cancelMatch(
            @PathVariable Long matchId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            matchService.cancelMatch(token, matchId);
            redirectAttributes.addFlashAttribute("successMsg", "Match cancelado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Não foi possível cancelar o match. Tente novamente.");
        }
        return "redirect:/pro/matches";
    }

    @GetMapping("/matches/{matchId}/review")
    public String reviewForm(
            @PathVariable Long matchId,
            HttpSession session,
            Model model) {
        String token = (String) session.getAttribute("token");
        MatchDTO match = matchService.getMatch(token, matchId);
        model.addAttribute("match", match);
        model.addAttribute("activePage", "matches");
        return "shared/review-form";
    }

    @GetMapping("/matches/{id}/history")
    @ResponseBody
    public ResponseEntity<?> getMatchHistory(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("token");
        try {
            return ResponseEntity.ok(matchService.getHistory(token, id));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao carregar histórico: " + e.getMessage());
        }
    }

    // Dados para a janela "Entrar em contato" — só liberado em match confirmado
    @GetMapping("/matches/{id}/contact")
    @ResponseBody
    public ResponseEntity<?> getMatchContact(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("token");
        try {
            MatchDTO match = matchService.getMatch(token, id);
            Long companyId = match.getProject().getCompany().getId();
            ContactInfoDTO contact = companyService.getContact(token, companyId);
            ContactCardDTO card = new ContactCardDTO(
                    match.getProject().getCompany().getCompanyName(),
                    match.getProject().getCompany().getProfilePhotoUrl(),
                    contact.getPhone(),
                    contact.getEmail(),
                    "/pro/company/" + companyId
            );
            return ResponseEntity.ok(card);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao carregar contato: " + e.getMessage());
        }
    }

    @PostMapping("/matches/{matchId}/review")
    public String submitReview(
            @PathVariable Long matchId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment,
            @RequestParam(value = "positiveReasons", required = false) List<String> positiveReasons,
            @RequestParam(value = "negativeReasons", required = false) List<String> negativeReasons,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            ReviewRequestDTO dto = new ReviewRequestDTO();
            dto.setRating(rating);
            dto.setComment(comment);
            dto.setAuthorType("PROFESSIONAL");
            dto.setPositiveReasons(positiveReasons != null ? positiveReasons : List.of());
            dto.setNegativeReasons(negativeReasons != null ? negativeReasons : List.of());
            reviewService.submit(token, matchId, dto);
            redirectAttributes.addFlashAttribute("successMsg", "Avaliação enviada com sucesso!");
        } catch (NexusApiException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao enviar avaliação: " + reviewErrorMessage(e));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Não foi possível conectar ao servidor. Tente novamente em instantes.");
        }
        return "redirect:/pro/matches";
    }

    private static String reviewErrorMessage(NexusApiException e) {
        String reason = e.getRawReason();
        if (reason == null) return e.getMessage();
        return switch (reason) {
            case "Please answer the match status check before reviewing." ->
                    "Responda antes se houve contato com a empresa nesse match, na tela de matches.";
            case "Reviews are not available when there was no contact." ->
                    "Avaliação indisponível: esse match foi marcado como sem contato.";
            case "Reviews are only allowed after a confirmed or rejected match." ->
                    "Só é possível avaliar depois que o match for confirmado ou recusado.";
            case "A review from this author type already exists for this match." ->
                    "Você já avaliou esse match.";
            default -> e.getMessage();
        };
    }

    @GetMapping("/opportunities")
    public String opportunities(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");

        // Recém-demonstrado interesse (redirect da própria página): flash attribute setado
        // em sendInterest, disponível aqui via merge automático do FlashMap no Model.
        Long justAppliedProjectId = (Long) model.asMap().get("justAppliedProjectId");

        // A vaga só continua na lista enquanto ainda está pendente (WAITING) — depois que o
        // interesse é enviado ela some, exceto nesta primeira renderização pós-redirect, pra
        // dar tempo do usuário ver o badge "Interesse demonstrado" antes de sumir de vez.
        List<MatchDTO> opportunities = professionalService.getOpportunities(token).stream()
                .filter(o -> "WAITING".equals(o.getStatus())
                        || (justAppliedProjectId != null
                            && o.getProject() != null
                            && justAppliedProjectId.equals(o.getProject().getId())))
                .collect(Collectors.toList());
        List<SkillDTO> allSkills = professionalService.getAllSkills(token);

        boolean showProfileWarning = false;
        boolean isAvailable = true;
        List<String> mySkills = List.of();
        try {
            ProfessionalProfileDTO profile = professionalService.getProfile(token);
            showProfileWarning = missingScoreRelevantFields(profile);
            isAvailable = profile.getAvailable() == null || profile.getAvailable();
            mySkills = profile.getSkills() != null ? profile.getSkills() : List.of();
        } catch (Exception e) {
            // Se o perfil não puder ser carregado, não bloqueia a página nem exibe o aviso.
        }

        model.addAttribute("opportunities", opportunities);
        model.addAttribute("allSkills", allSkills);
        model.addAttribute("showProfileWarning", showProfileWarning);
        model.addAttribute("isAvailable", isAvailable);
        model.addAttribute("mySkills", mySkills);
        model.addAttribute("activePage", "opportunities");
        return "pro/pro-opportunities";
    }

    // Mesmos campos que pesam no cálculo do score (skills, pretensão salarial do(s)
    // regime(s) escolhido(s) e localização) — não é a mesma checagem de "perfil completo"
    // usada em pro-profile (que também exige GitHub/nível de experiência).
    private boolean missingScoreRelevantFields(ProfessionalProfileDTO profile) {
        if (profile == null) return true;

        boolean noSkills = profile.getSkills() == null || profile.getSkills().isEmpty();
        boolean noLocation = profile.getLatitude() == null || profile.getLongitude() == null;

        boolean missingSalary;
        List<String> types = profile.getPreferredOpportunityTypes();
        if (types == null || types.isEmpty()) {
            missingSalary = true;
        } else {
            missingSalary = types.stream().anyMatch(type ->
                    ("JOB".equals(type)
                            && (profile.getExpectedSalaryCLT() == null || profile.getExpectedSalaryPJ() == null))
                    || ("PROJECT".equals(type)
                            && (profile.getFreelanceMinExpectation() == null || profile.getFreelanceMaxExpectation() == null)));
        }

        return noSkills || noLocation || missingSalary;
    }

    @PostMapping("/opportunities/{projectId}/interest")
    public String sendInterest(
            @PathVariable Long projectId,
            @RequestParam(required = false) String redirectTo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            professionalService.sendInterest(token, projectId);
            redirectAttributes.addFlashAttribute("successMsg", "Interesse enviado com sucesso!");
            redirectAttributes.addFlashAttribute("justAppliedProjectId", projectId);
        } catch (NexusApiException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao enviar interesse: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Não foi possível conectar ao servidor. Tente novamente em instantes.");
        }
        if ("list".equals(redirectTo)) {
            return "redirect:/pro/opportunities";
        }
        return "redirect:/public/opportunity/" + projectId;
    }

    @GetMapping("/companies")
    public String companiesDirectory(Model model) {
        model.addAttribute("activePage", "companies");
        return "pro/pro-companies";
    }

    @GetMapping("/professionals")
    public String professionalsDirectory(Model model) {
        model.addAttribute("activePage", "professionals");
        return "pro/pro-professionals";
    }

    @GetMapping("/map")
    public String map(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String uf,
            @RequestParam(required = false) String type,
            HttpSession session,
            Model model) {
        String token = (String) session.getAttribute("token");
        double userLat = -23.5505;
        double userLng = -46.6333;
        Double myLat = null;
        Double myLng = null;
        try {
            ProfessionalProfileDTO profile = professionalService.getProfile(token);
            if (profile.getLatitude() != null) userLat = profile.getLatitude();
            if (profile.getLongitude() != null) userLng = profile.getLongitude();
            myLat = profile.getLatitude();
            myLng = profile.getLongitude();
        } catch (Exception e) {
            // mantém o fallback
        }
        try {
            List<MapProfessionalDTO> professionals = mapService.getProfessionals(token, city, uf, type);
            List<MapCompanyDTO> companies = mapService.getCompanies(token, city, uf);
            List<MapOpportunityDTO> opportunities = mapService.getOpportunities(token, city, uf, null);
            model.addAttribute("professionalsJson", professionals);
            model.addAttribute("companiesJson", companies);
            model.addAttribute("opportunitiesJson", opportunities);
            if (city != null && !city.isBlank()) {
                double[] center = MapService.computeCenter(professionals, companies, opportunities, userLat, userLng);
                userLat = center[0];
                userLng = center[1];
            }
        } catch (Exception e) {
            model.addAttribute("professionalsJson", List.of());
            model.addAttribute("companiesJson", List.of());
            model.addAttribute("opportunitiesJson", List.of());
        }
        model.addAttribute("userLat", userLat);
        model.addAttribute("userLng", userLng);
        model.addAttribute("myLat", myLat);
        model.addAttribute("myLng", myLng);
        model.addAttribute("cityFilter", city != null ? city : "");
        model.addAttribute("ufFilter",   uf   != null ? uf   : "");
        model.addAttribute("allSkills", professionalService.getAllSkills(token));
        model.addAttribute("activePage", "map");
        return "pro/pro-map";
    }

    @PostMapping("/resume")
    public String uploadResume(
            @RequestParam("file") MultipartFile file,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMsg", "Selecione um arquivo PDF.");
                return "redirect:/pro/profile";
            }
            if (!"application/pdf".equals(file.getContentType())) {
                redirectAttributes.addFlashAttribute("errorMsg", "Somente arquivos PDF são aceitos.");
                return "redirect:/pro/profile";
            }
            professionalService.uploadResume(token, file);
            redirectAttributes.addFlashAttribute("successMsg", "Currículo enviado com sucesso!");
        } catch (NexusApiException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao enviar currículo: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Não foi possível conectar ao servidor. Tente novamente em instantes.");
        }
        return "redirect:/pro/profile";
    }

    @GetMapping("/{professionalId}/resume")
    public void downloadResume(
            @PathVariable Long professionalId,
            HttpSession session,
            HttpServletResponse response) {
        String token = (String) session.getAttribute("token");
        try {
            byte[] content = professionalService.downloadResume(token, professionalId);
            response.setContentType("application/pdf");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"curriculo.pdf\"");
            response.getOutputStream().write(content);
            response.getOutputStream().flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @GetMapping("/analytics")
    public String analytics(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        try {
            ProfessionalDashboardAnalyticsDTO analytics = professionalService.getAnalytics(token);
            if (analytics.getMatchSummary() != null) analytics.getMatchSummary().recomputeAcceptanceRate();
            if (analytics.getAcceptanceRatePerCompany() != null) {
                analytics.getAcceptanceRatePerCompany().forEach(CompanyAcceptanceRateDTO::recomputeAcceptanceRate);
            }
            model.addAttribute("analytics", analytics);

            model.addAttribute("monthlyMatches", analytics.getMatchesPerMonth());
            model.addAttribute("scoreDistribution", analytics.getScoreDistribution());
            model.addAttribute("companyRates", analytics.getAcceptanceRatePerCompany());
            model.addAttribute("skillDemand", analytics.getMostRequiredSkills());
        } catch (Exception e) {
            model.addAttribute("analytics", null);
            model.addAttribute("monthlyMatches", List.of());
            model.addAttribute("scoreDistribution", List.of());
            model.addAttribute("companyRates", List.of());
            model.addAttribute("skillDemand", List.of());
            model.addAttribute("errorMsg", "Não foi possível carregar os dados analíticos.");
        }

        model.addAttribute("activePage", "analytics");
        return "pro/pro-analytics";
    }

    @GetMapping("/profile/export")
    @ResponseBody
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(required = false) Long professionalId,
            HttpSession session) {
        String token = (String) session.getAttribute("token");
        byte[] pdf = professionalService.exportPdf(token, professionalId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"perfil-nexus.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/profile/photo")
    @ResponseBody
    public ResponseEntity<String> uploadProPhoto(
            @RequestParam("file") MultipartFile file,
            HttpSession session) {
        String token = (String) session.getAttribute("token");
        try {
            String url = professionalService.uploadPhoto(token, file);
            session.setAttribute("profilePhotoUrl", url);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao fazer upload: " + e.getMessage());
        }
    }

    @DeleteMapping("/profile/photo")
    @ResponseBody
    public ResponseEntity<String> removeProPhoto(HttpSession session) {
        String token = (String) session.getAttribute("token");
        try {
            professionalService.removePhoto(token);
            session.removeAttribute("profilePhotoUrl");
            return ResponseEntity.ok("Foto removida.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao remover: " + e.getMessage());
        }
    }

    // Chamado via fetch() pela página de perfil — atualiza a seção do GitHub sem recarregar
    @DeleteMapping("/profile/github")
    @ResponseBody
    public ResponseEntity<String> unlinkGithub(HttpSession session) {
        String token = (String) session.getAttribute("token");
        try {
            professionalService.unlinkGithub(token);
            return ResponseEntity.ok("Conta do GitHub desconectada.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao desconectar: " + e.getMessage());
        }
    }
}
