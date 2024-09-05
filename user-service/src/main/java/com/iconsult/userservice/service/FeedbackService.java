package com.iconsult.userservice.service;

import com.iconsult.userservice.model.dto.request.FeedbackRequestDTO;
import com.iconsult.userservice.model.dto.response.FeedbackResponseDTO;
import com.iconsult.userservice.model.entity.Feedback;
import com.zanbeel.customUtility.model.CustomResponseEntity;

import java.util.List;

public interface FeedbackService {

//     CustomResponseEntity<FeedbackResponseDTO> createFeedback(FeedbackRequestDTO feedbackRequestDTO);
//     List<CustomResponseEntity<FeedbackResponseDTO>> getFeedbacksByCustomerId(Long customerId);
//     CustomResponseEntity<FeedbackResponseDTO> getFeedbackById(Long id);
//     void deleteFeedback(Long id);
//     CustomResponseEntity<FeedbackResponseDTO> convertToDTO(Feedback feedback);


     FeedbackResponseDTO createFeedback(FeedbackRequestDTO feedbackRequestDTO);

     List<FeedbackResponseDTO> getFeedbacksByCustomerId(Long customerId);

     FeedbackResponseDTO getFeedbackById(Long id);

     void deleteFeedback(Long id);

//     FeedbackResponseDTO convertToDTO(Feedback feedback);

     List<FeedbackResponseDTO> getAllFeedbacks();

}

