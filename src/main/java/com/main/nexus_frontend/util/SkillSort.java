package com.main.nexus_frontend.util;

import com.main.nexus_frontend.model.SkillDTO;
import java.util.Comparator;
import java.util.List;

// Usado pelas telas de matches/oportunidades pra reordenar os chips de "skills exigidas":
// as que o profissional já tem (azul) primeiro, depois as que faltam (vermelho claro).
// Comparator.comparing com sorted() é estável, então a ordem original dentro de cada
// grupo (como veio do backend) é preservada.
public final class SkillSort {

    private SkillSort() {}

    public static List<SkillDTO> byMatch(List<SkillDTO> requiredSkills, List<String> mySkills) {
        if (requiredSkills == null) return List.of();
        List<String> mine = mySkills != null ? mySkills : List.of();
        return requiredSkills.stream()
                .sorted(Comparator.comparing(sk -> !mine.contains(sk.getName())))
                .toList();
    }
}
