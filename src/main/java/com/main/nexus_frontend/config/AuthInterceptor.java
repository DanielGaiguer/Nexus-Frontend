package com.main.nexus_frontend.config;

import com.main.nexus_frontend.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private NotificationService notificationService;

    // preHandle roda antes do controller
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        HttpSession session = request.getSession(false);

        String token = (session != null) ? (String) session.getAttribute("token") : null;
        String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;

        // Se nao tem token de sessao, redireciona para auth/login
        // Preservando a URL original como parametro redirect, assim, o usuario volta para a pagina que estava tentando acessar antes de fazer login
        if (token == null) {
            String queryString = request.getQueryString();
            String originalUrl = uri + (queryString != null ? "?" + queryString : "");
            String encodedRedirect = URLEncoder.encode(originalUrl, StandardCharsets.UTF_8);
            response.sendRedirect("/auth/login?redirect=" + encodedRedirect);
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

        // Status check é respondido só pela empresa; review é aberto a empresa e profissional
        if (uri.contains("/status-check") && !"COMPANY".equals(userRole)) {
            response.sendRedirect("/");
            return false;
        }

        return true;
    }

    //roda depois do controller, antes da renderização
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView == null) return;

        HttpSession session = request.getSession(false);
        String token = (session != null) ? (String) session.getAttribute("token") : null;
        if (token == null) return;

        // injeta a contagem de notificações não lidas
        try {
            Integer unreadCount = notificationService.getUnreadCount(token);
            modelAndView.addObject("unreadCount", unreadCount);
        } catch (Exception e) {
            modelAndView.addObject("unreadCount", 0);
        }
    }
}
