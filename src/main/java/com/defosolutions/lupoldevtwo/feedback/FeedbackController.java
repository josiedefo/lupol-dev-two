package com.defosolutions.lupoldevtwo.feedback;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class FeedbackController {

    private static final Logger log = Logger.getLogger(FeedbackController.class.getName());

    @PostMapping("/feedback")
    public ResponseEntity<Void> feedback(@RequestBody FeedbackDTO dto) {
        String safeComment = dto.comment() != null ? dto.comment().replace("\n", "\\n") : "";
        log.info("Feedback from " + dto.visitorId() + ": helpful=" + dto.helpful() + " comment=" + safeComment);
        return ResponseEntity.accepted().build();
    }
}
