package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.exception.NexusAuthException;
import com.main.nexus_frontend.model.*;
import com.main.nexus_frontend.service.AuthService;
import com.main.nexus_frontend.service.CompanyService;
import com.main.nexus_frontend.service.ProfessionalService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private ProfessionalService professionalService;
    @Autowired
    private CompanyService companyService;

    @Value("${nexus.api.base-url}")
    private String apiBaseUrl;

    // ── Login ────────────────────────────────────────────────

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String linkedinError,
            @RequestParam(required = false) String redirect,
            Model model) {
        model.addAttribute("loginRequest", new LoginRequestDTO("", ""));
        model.addAttribute("linkedinError", linkedinError);
        model.addAttribute("redirect", redirect);
        return "login";
    }

    @PostMapping("/login")
    public String login(LoginRequestDTO request,
                        @RequestParam(required = false) String redirect,
                        HttpSession session,
                        Model model) {
        try {
            LoginResponseDTO response = authService.login(request);
            populateSession(session, response);
            if (isSafeRedirect(redirect)) {
                return "redirect:" + redirect;
            }
            return redirectForRole(response.getRole());

        } catch (NexusAuthException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("loginRequest", request);
            model.addAttribute("redirect", redirect);
            return "login";
        } catch (Exception e) {
            model.addAttribute("error",
                "Não foi possível conectar ao servidor. Tente novamente em instantes.");
            model.addAttribute("loginRequest", request);
            model.addAttribute("redirect", redirect);
            return "login";
        }
    }

    // Só aceita caminhos internos (evita open redirect para domínios externos)
    private boolean isSafeRedirect(String redirect) {
        return redirect != null
                && redirect.startsWith("/")
                && !redirect.startsWith("//")
                && !redirect.contains("://");
    }

    private void populateSession(HttpSession session, LoginResponseDTO response) {
        session.setAttribute("token",    response.getToken());
        session.setAttribute("userName", response.getName());
        session.setAttribute("userRole", response.getRole());
        session.setAttribute("userId",   response.getUserId());

        // Carrega foto de perfil na sessão (tenta, não quebra o login se falhar)
        try {
            if ("PROFESSIONAL".equals(response.getRole())) {
                var profile = professionalService.getProfile(response.getToken());
                if (profile != null && profile.getProfilePhotoUrl() != null
                        && !profile.getProfilePhotoUrl().isBlank()) {
                    session.setAttribute("profilePhotoUrl", profile.getProfilePhotoUrl());
                }
            } else if ("COMPANY".equals(response.getRole())) {
                var profile = companyService.getProfile(response.getToken());
                if (profile != null && profile.getProfilePhotoUrl() != null
                        && !profile.getProfilePhotoUrl().isBlank()) {
                    session.setAttribute("profilePhotoUrl", profile.getProfilePhotoUrl());
                }
            }
        } catch (Exception ignored) {}
    }

    private String redirectForRole(String role) {
        if ("ADMIN".equals(role)) return "redirect:/admin/dashboard";
        if ("COMPANY".equals(role)) return "redirect:/company/dashboard";
        return "redirect:/pro/dashboard";
    }

    // ── Sign In with LinkedIn (OpenID Connect) ───────────────
    // O client_secret nunca sai do backend; aqui só redirecionamos o
    // navegador para o backend, que fala com o LinkedIn e devolve o
    // resultado para /auth/linkedin/complete (login) ou para a página de
    // perfil (vínculo de conta já logada).

    @GetMapping("/linkedin/login")
    public String linkedInLogin(@RequestParam(required = false) String redirect) {
        String url = apiBaseUrl + "/auth/linkedin/login";
        if (isSafeRedirect(redirect)) {
            url += "?redirect=" + java.net.URLEncoder.encode(redirect, java.nio.charset.StandardCharsets.UTF_8);
        }
        return "redirect:" + url;
    }

    @GetMapping("/linkedin/register")
    public String linkedInRegister(@RequestParam String role) {
        return "redirect:" + apiBaseUrl + "/auth/linkedin/register?role=" + role;
    }

    @GetMapping("/linkedin/link")
    public String linkedInLink(HttpSession session) {
        Object token = session.getAttribute("token");
        if (token == null) {
            return "redirect:/auth/login";
        }
        return "redirect:" + apiBaseUrl + "/auth/linkedin/link?token=" + token;
    }

    @GetMapping("/linkedin/complete")
    public String linkedInComplete(
            @RequestParam String token,
            @RequestParam Long userId,
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String role,
            @RequestParam(required = false) String redirect,
            HttpSession session) {
        LoginResponseDTO response = new LoginResponseDTO(userId, email, name, role, token);
        populateSession(session, response);
        if (isSafeRedirect(redirect)) {
            return "redirect:" + redirect;
        }
        return redirectForRole(role);
    }

    // ── Registro de profissional ─────────────────────────────

    @GetMapping("/register/professional")
    public String registerProfessionalPage(Model model) {
        model.addAttribute("request", new RegisterProfessionalRequestDTO());
        return "register-professional";
    }

    @PostMapping("/register/professional")
    public String registerProfessional(RegisterProfessionalRequestDTO request,
                                       RedirectAttributes redirectAttributes) {
        try {
            authService.registerProfessional(request);
            redirectAttributes.addFlashAttribute("success",
                "Conta criada com sucesso! Faça login para começar.");
            return "redirect:/auth/login";

        } catch (NexusAuthException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/register/professional";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Não foi possível conectar ao servidor. Tente novamente em instantes.");
            return "redirect:/auth/register/professional";
        }
    }

    // ── Registro de empresa ──────────────────────────────────

    @GetMapping("/register/company")
    public String registerCompanyPage(Model model) {
        model.addAttribute("request", new RegisterCompanyRequestDTO());
        return "register-company";
    }

    @PostMapping("/register/company")
    public String registerCompany(RegisterCompanyRequestDTO request,
                                  RedirectAttributes redirectAttributes) {
        try {
            authService.registerCompany(request);
            return "redirect:/auth/register/company/success";

        } catch (NexusAuthException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/register/company";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Não foi possível conectar ao servidor. Tente novamente em instantes.");
            return "redirect:/auth/register/company";
        }
    }

    @GetMapping("/register/company/success")
    public String registerCompanySuccess() {
        return "register-success";
    }

    // ── Finalização do cadastro de empresa via LinkedIn ──────
    // O LinkedIn autentica a pessoa, mas não fornece razão social — esta
    // etapa curta pede só o que falta, usando o ticket assinado emitido
    // pelo backend logo após o callback do OAuth.

    @GetMapping("/register/company/linkedin")
    public String registerCompanyLinkedInPage(
            @RequestParam String ticket,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String name,
            Model model) {
        model.addAttribute("ticket", ticket);
        model.addAttribute("email", email);
        model.addAttribute("name", name);
        return "register-company-linkedin";
    }

    @PostMapping("/register/company/linkedin")
    public String registerCompanyLinkedIn(
            @RequestParam String ticket,
            @RequestParam String companyName,
            @RequestParam(required = false) String taxId,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String cep,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {
        try {
            RegisterCompanyLinkedInRequestDTO request = new RegisterCompanyLinkedInRequestDTO();
            request.setTicket(ticket);
            request.setCompanyName(companyName);
            request.setTaxId(taxId);
            request.setPhone(phone);
            request.setCep(cep);
            request.setDescription(description);
            authService.registerCompanyViaLinkedIn(request);
            return "redirect:/auth/register/company/success";

        } catch (NexusAuthException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addAttribute("ticket", ticket);
            return "redirect:/auth/register/company/linkedin";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Não foi possível conectar ao servidor. Tente novamente em instantes.");
            redirectAttributes.addAttribute("ticket", ticket);
            return "redirect:/auth/register/company/linkedin";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
