package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.model.NotificationSummaryDTO;
import com.main.nexus_frontend.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    @ResponseBody
    public ResponseEntity<NotificationSummaryDTO> getAll(HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(notificationService.getAll(token));
    }

    @PostMapping("/read-all")
    @ResponseBody
    public ResponseEntity<String> markAllRead(HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return ResponseEntity.status(401).build();
        notificationService.markAllRead(token);
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/{id}/read")
    @ResponseBody
    public ResponseEntity<String> markRead(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return ResponseEntity.status(401).build();
        notificationService.markRead(token, id);
        return ResponseEntity.ok("ok");
    }
}
