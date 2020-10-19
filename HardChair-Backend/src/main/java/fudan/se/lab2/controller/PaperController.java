package fudan.se.lab2.controller;

import fudan.se.lab2.controller.request.OpenFinalResultRequest;
import fudan.se.lab2.controller.request.ReviseOrConfirmReviewResultRequest;
import fudan.se.lab2.controller.request.SubmitPostRequest;
import fudan.se.lab2.controller.request.SubmitRebuttalRequest;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.service.PaperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PaperController {
    private static final String MESSAGE  = "message";

    Logger logger = LoggerFactory.getLogger(PaperController.class);
    private PaperService paperService;

    @Autowired
    public PaperController(PaperService paperService){
        this.paperService = paperService;
    }

    @PostMapping("/SubmitPost")
    public ResponseEntity<Map<String, String>> submitPost(@RequestBody SubmitPostRequest request){
        logger.debug("submit post");

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String message = paperService.submitPost(request.getPostContent(), user.getUsername(), request.getPaperId(), request.getQuoteId());
        Map<String, String> res = new HashMap<>();
        res.put(MESSAGE, message);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/SubmitRebuttal")
    public ResponseEntity<Map<String, String>> submitRebuttal(@RequestBody SubmitRebuttalRequest request){
        logger.debug("submit rebuttal");

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String message = paperService.submitRebuttal(request.getRebuttalContent(), user.getUsername(), request.getPaperId());
        Map<String, String> res = new HashMap<>();
        res.put(MESSAGE, message);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/ReviseOrConfirmReviewResult")
    public ResponseEntity<Map<String, String>> reviseOrConfirmReviewResult(@RequestBody ReviseOrConfirmReviewResultRequest request){
        logger.debug("revise or review result");

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String message = paperService.reviseOrConfirmReviewResult(request.getScore(), request.getComment(), request.getConfidence(), request.getReviewResultId(), user.getUsername());
        Map<String, String> res = new HashMap<>();
        res.put(MESSAGE, message);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/OpenFinalResult")
    public ResponseEntity<Map<String, String>> openFinalResult(@RequestBody OpenFinalResultRequest request){
        logger.debug("open final result");

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String message = paperService.openFinalResult(request.getConferenceId(), user.getUsername());
        Map<String, String> res = new HashMap<>();
        res.put(MESSAGE, message);
        return ResponseEntity.ok(res);
    }
}
