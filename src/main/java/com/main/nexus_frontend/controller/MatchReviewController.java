package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.exception.NexusApiException;
import com.main.nexus_frontend.model.MatchDTO;
import com.main.nexus_frontend.model.ReviewRequestDTO;
import com.main.nexus_frontend.service.MatchService;
import com.main.nexus_frontend.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/matches")
public class MatchReviewController {

    private static final String NEEDS_STATUS_CHECK_MSG =
            "Please answer the match status check before reviewing.";
    private static final String NO_CONTACT_MSG =
            "Reviews are not available when there was no contact.";

    @Autowired
    private MatchService matchService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/{matchId}/review")
    public String reviewForm(@PathVariable Long matchId, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        MatchDTO match = matchService.getMatch(token, matchId);
        model.addAttribute("match", match);
        return "matches/review";
    }

    @PostMapping("/{matchId}/review")
    public String submitReview(
            @PathVariable Long matchId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment,
            @RequestParam(value = "positiveReasons", required = false) List<String> positiveReasons,
            @RequestParam(value = "negativeReasons", required = false) List<String> negativeReasons,
            HttpSession session,
            Model model) {
        String token = (String) session.getAttribute("token");
        String userRole = (String) session.getAttribute("userRole");

        ReviewRequestDTO dto = new ReviewRequestDTO();
        dto.setRating(rating);
        dto.setComment(comment);
        dto.setAuthorType(userRole);
        dto.setPositiveReasons(positiveReasons != null ? positiveReasons : List.of());
        dto.setNegativeReasons(negativeReasons != null ? negativeReasons : List.of());

        try {
            reviewService.submit(token, matchId, dto);
            model.addAttribute("match", matchService.getMatch(token, matchId));
            model.addAttribute("submitted", true);
            return "matches/review";
        } catch (NexusApiException e) {
            model.addAttribute("match", matchService.getMatch(token, matchId));
            String reason = e.getRawReason();
            if (NEEDS_STATUS_CHECK_MSG.equals(reason)) {
                model.addAttribute("needsStatusCheck", true);
            } else if (NO_CONTACT_MSG.equals(reason)) {
                model.addAttribute("noContact", true);
            } else {
                model.addAttribute("errorMsg", "Não foi possível enviar a avaliação. Tente novamente.");
            }
            return "matches/review";
        }
    }
}
