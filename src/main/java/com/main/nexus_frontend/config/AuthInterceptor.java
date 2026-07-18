package com.main.nexus_frontend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        HttpSession session = request.getSession(false);

        String token = (session != null) ? (String) session.getAttribute("token") : null;
        String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;

        if (token == null) {
            response.sendRedirect("/auth/login");
            return false;
        }

        if (uri.startsWith("/pro") && !"PROFESSIONAL".equals(userRole)) {
            response.sendRedirect("/");
            return false;
        }

        if (uri.startsWith("/company") && !"COMPANY".equals(userRole)) {
            response.sendRedirect("/");
            return false;
        }

        if (uri.startsWith("/admin") && !"ADMIN".equals(userRole)) {
            response.sendRedirect("/");
            return false;
        }

        return true;
    }
}
