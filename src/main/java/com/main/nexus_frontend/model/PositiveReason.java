package com.main.nexus_frontend.model;

// Espelha com.main.nexus.model.enums.PositiveReason (backend). Fonte única dos chips de
// "pontos positivos" na avaliação de match — antes cada tela (shared/review-form.html,
// matches/review.html, company-dashboard.html, pro-dashboard.html) reimplementava essa
// lista + tradução na mão via Thymeleaf inline, then divergia. Quem adicionar um valor no
// enum do backend precisa espelhar aqui também (os dois projetos não compartilham código),
// mas agora só nesse UM lugar — não mais em 4 templates.
public enum PositiveReason {
    EXCELLENT_COMMUNICATION("Excelente comunicação"),
    HIGH_TECHNICAL_SKILL("Alta competência técnica"),
    DELIVERED_ON_TIME("Entregou no prazo"),
    TEAM_PLAYER("Trabalho em equipe"),
    PROACTIVE("Proativo"),
    EXCEEDED_EXPECTATIONS("Superou expectativas"),
    RELIABLE("Confiável"),
    PUNCTUAL("Pontual"),
    HIGH_CODE_QUALITY("Alta qualidade de código"),
    GOOD_PROBLEM_SOLVING("Boa resolução de problemas");

    private final String label;

    PositiveReason(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
