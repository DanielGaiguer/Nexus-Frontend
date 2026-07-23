package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.exception.NexusAuthException;
import com.main.nexus_frontend.model.*;
import com.main.nexus_frontend.service.AuthService;
import com.main.nexus_frontend.service.CompanyService;
import com.main.nexus_frontend.service.ProfessionalService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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

    // ── Login ────────────────────────────────────────────────

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequestDTO("", ""));
        return "login";
    }

    @PostMapping("/login")
    public String login(LoginRequestDTO request,
                        HttpSession session,
                        Model model) {
        try {
            LoginResponseDTO response = authService.login(request);
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

            if ("ADMIN".equals(response.getRole())) return "redirect:/admin/dashboard";
            if ("COMPANY".equals(response.getRole())) return "redirect:/company/dashboard";
            return "redirect:/pro/dashboard";

        } catch (NexusAuthException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("loginRequest", request);
            return "login";
        } catch (Exception e) {
            model.addAttribute("error",
                "Não foi possível conectar ao servidor. Tente novamente em instantes.");
            model.addAttribute("loginRequest", request);
            return "login";
        }
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

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
