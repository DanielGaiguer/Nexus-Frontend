package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.model.CompanyDirectoryPageDTO;
import com.main.nexus_frontend.model.ProfessionalDirectoryPageDTO;
import com.main.nexus_frontend.service.DirectoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

// Endpoints de dados (JSON) chamados pelo JS vanilla das telas de diretório (Empresas/Profissionais).
// O navegador nunca fala direto com o backend :8081 — passa sempre por aqui.
@Controller
@RequestMapping("/app-api/directory")
public class DirectoryBffController {

    @Autowired
    private DirectoryService directoryService;

    @GetMapping("/companies")
    @ResponseBody
    public ResponseEntity<CompanyDirectoryPageDTO> getCompanies(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            HttpSession session) {
        if (session.getAttribute("token") == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            return ResponseEntity.ok(directoryService.getCompanies(search, page, size));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @GetMapping("/professionals")
    @ResponseBody
    public ResponseEntity<ProfessionalDirectoryPageDTO> getProfessionals(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            HttpSession session) {
        if (session.getAttribute("token") == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            return ResponseEntity.ok(directoryService.getProfessionals(search, page, size));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }
}
