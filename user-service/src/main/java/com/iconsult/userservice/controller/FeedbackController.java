package com.iconsult.userservice.controller;

import com.iconsult.userservice.model.dto.request.FeedbackRequestDTO;
import com.iconsult.userservice.model.dto.response.FeedbackResponseDTO;
import com.iconsult.userservice.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/createFeedback")
    public ResponseEntity<FeedbackResponseDTO> createFeedback(@RequestBody FeedbackRequestDTO feedbackRequestDTO) {
        FeedbackResponseDTO feedbackResponseDTO = feedbackService.createFeedback(feedbackRequestDTO);
        return new ResponseEntity<>(feedbackResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<FeedbackResponseDTO>> getFeedbacksByCustomerId(@PathVariable Long customerId) {
        List<FeedbackResponseDTO> feedbacks = feedbackService.getFeedbacksByCustomerId(customerId);
        return new ResponseEntity<>(feedbacks, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponseDTO> getFeedbackById(@PathVariable Long id) {
        FeedbackResponseDTO feedbackResponseDTO = feedbackService.getFeedbackById(id);
        return new ResponseEntity<>(feedbackResponseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/all")
    public ResponseEntity<List<FeedbackResponseDTO>> getAllFeedbacks() {
        List<FeedbackResponseDTO> feedbacks = feedbackService.getAllFeedbacks();
        return new ResponseEntity<>(feedbacks, HttpStatus.OK);
    }
}

