package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.model.SkillDTO;
import com.main.nexus_frontend.service.SkillBffService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

// Endpoints de dados (JSON) usados pelo nexus-skill-select.js — o componente reutilizável
// de seleção/sugestão de skills usado no perfil do profissional e na oportunidade da
// empresa. Leitura é pública (skills são catálogo público); sugerir uma nova exige login.
@Controller
@RequestMapping("/app-api/skills")
public class SkillBffController {

    @Autowired
    private SkillBffService skillBffService;

    @GetMapping
    @ResponseBody
    public List<SkillDTO> getAllSkills(HttpSession session) {
        return skillBffService.getAllSkills((String) session.getAttribute("token"));
    }

    @GetMapping("/categories")
    @ResponseBody
    public List<String> getCategories(HttpSession session) {
        return skillBffService.getCategories((String) session.getAttribute("token"));
    }

    @PostMapping("/suggest")
    @ResponseBody
    public ResponseEntity<SkillDTO> suggestSkill(@RequestBody Map<String, String> body, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            return skillBffService.suggestSkill(body.get("name"), body.get("category"), token);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }
}
