package com.main.nexus_frontend.model;

// Espelha com.main.nexus.model.enums.NegativeReason (backend). Ver PositiveReason para o
// motivo de existir — mesma centralização, agora para os chips de "pontos negativos".
public enum NegativeReason {
    MISSED_DEADLINES("Atrasou prazos"),
    POOR_COMMUNICATION("Comunicação ruim"),
    LOW_CODE_QUALITY("Baixa qualidade de código"),
    UNPROFESSIONAL("Pouco profissional"),
    ABSENT("Ausente"),
    UNRELIABLE("Não confiável"),
    POOR_PROBLEM_SOLVING("Dificuldade para resolver problemas"),
    DID_NOT_MEET_EXPECTATIONS("Não atendeu expectativas"),
    OTHER("Outro");

    private final String label;

    NegativeReason(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
