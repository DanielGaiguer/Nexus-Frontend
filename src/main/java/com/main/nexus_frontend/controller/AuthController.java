package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.model.LoginRequestDTO;
import com.main.nexus_frontend.model.LoginResponseDTO;
import com.main.nexus_frontend.model.RegisterCompanyRequestDTO;
import com.main.nexus_frontend.model.RegisterProfessionalRequestDTO;
import com.main.nexus_frontend.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequestDTO("", ""));
        return "login";
    }

    @PostMapping("/login")
    public String login(LoginRequestDTO request, HttpSession session, Model model) {
        try {
            LoginResponseDTO response = authService.login(request);
            session.setAttribute("token", response.getToken());
            session.setAttribute("userName", response.getName());
            session.setAttribute("userRole", response.getRole());
            session.setAttribute("userId", response.getUserId());

            if ("ADMIN".equals(response.getRole())) {
                return "redirect:/admin/dashboard";
            } else if ("COMPANY".equals(response.getRole())) {
                return "redirect:/company/dashboard";
            } else if ("PROFESSIONAL".equals(response.getRole())) {
                return "redirect:/pro/dashboard";
            }
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid email or password.");
            model.addAttribute("loginRequest", request);
            return "login";
        }
    }

    @GetMapping("/register/professional")
    public String registerProfessionalPage(Model model) {
        model.addAttribute("request", new RegisterProfessionalRequestDTO());
        return "register-professional";
    }

    @PostMapping("/register/professional")
    public String registerProfessional(
            RegisterProfessionalRequestDTO request,
            RedirectAttributes redirectAttributes) {
        try {
            authService.registerProfessional(request);
            redirectAttributes.addFlashAttribute("success",
                    "Registration successful! You can now log in.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Registration failed. Email may already be in use.");
            return "redirect:/auth/register/professional";
        }
    }

    @GetMapping("/register/company")
    public String registerCompanyPage(Model model) {
        model.addAttribute("request", new RegisterCompanyRequestDTO());
        return "register-company";
    }

    @PostMapping("/register/company")
    public String registerCompany(
            RegisterCompanyRequestDTO request,
            RedirectAttributes redirectAttributes) {
        try {
            authService.registerCompany(request);
            return "redirect:/auth/register/company/success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Registration failed. Email may already be in use.");
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
