package com.main.nexus_frontend.config;

import com.main.nexus_frontend.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private NotificationService notificationService;

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

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView == null) return;

        HttpSession session = request.getSession(false);
        String token = (session != null) ? (String) session.getAttribute("token") : null;
        if (token == null) return;

        try {
            Integer unreadCount = notificationService.getUnreadCount(token);
            modelAndView.addObject("unreadCount", unreadCount);
        } catch (Exception e) {
            modelAndView.addObject("unreadCount", 0);
        }
    }
}
