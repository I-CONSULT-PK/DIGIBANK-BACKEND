package com.iconsult.userservice.service.Impl;

import com.iconsult.userservice.model.dto.request.FeedbackRequestDTO;
import com.iconsult.userservice.model.dto.response.FeedbackResponseDTO;
import com.iconsult.userservice.model.entity.Customer;
import com.iconsult.userservice.model.entity.Feedback;
import com.iconsult.userservice.repository.CustomerRepository;
import com.iconsult.userservice.repository.FeedbackRepository;
import com.iconsult.userservice.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public FeedbackResponseDTO createFeedback(FeedbackRequestDTO feedbackRequestDTO) {

        Optional<Customer> customerOpt = customerRepository.findById(feedbackRequestDTO.getCustomerId());
        if (!customerOpt.isPresent()) {
            throw new IllegalArgumentException("Customer not found");
        }

        Customer customer = customerOpt.get();

        // Get the start and end of the current month
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startOfMonth = calendar.getTime();

        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        Date endOfMonth = calendar.getTime();

        // Check if there's already feedback for the customer in the current month
        Optional<Feedback> existingFeedbackOpt = feedbackRepository.findByCustomerIdAndTimestampBetween(
                customer.getId(), startOfMonth, endOfMonth
        );

        if (existingFeedbackOpt.isPresent()) {
            throw new IllegalArgumentException("Customer can only provide one feedback per month");
        }

        int rating = feedbackRequestDTO.getRating();
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Feedback feedback = new Feedback();
        feedback.setCustomer(customer);
        feedback.setMessage(feedbackRequestDTO.getMessage());
        feedback.setRating(rating);
        feedback.setTimestamp(new Date());

        Feedback savedFeedback = feedbackRepository.save(feedback);

        return convertToDTO(savedFeedback);

        /*// Check if the customer has already provided feedback
        Optional<Feedback> existingFeedback = feedbackRepository.findByCustomerId(feedbackRequestDTO.getCustomerId());
        if (existingFeedback.isPresent()) {
            throw new IllegalArgumentException("Customer has already provided feedback");
        }

        int rating = feedbackRequestDTO.getRating();
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Feedback feedback = new Feedback();
        feedback.setCustomer(customerOpt.get());
        feedback.setMessage(feedbackRequestDTO.getMessage());
        feedback.setRating(feedbackRequestDTO.getRating());
        feedback.setTimestamp(new Date());

        Feedback savedFeedback = feedbackRepository.save(feedback);

        return convertToDTO(savedFeedback);
*/
    }

    @Override
    public List<FeedbackResponseDTO> getFeedbacksByCustomerId(Long customerId) {
//        List<Feedback> feedbacks = feedbackRepository.findByCustomerId(customerId);
//        return feedbacks.stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
        Optional<Feedback> feedbackOpt = feedbackRepository.findByCustomerId(customerId);

        if (feedbackOpt.isEmpty()) {
            return Collections.emptyList(); // Return an empty list if no feedback found
        }

        Feedback feedback = feedbackOpt.get();
        return Collections.singletonList(convertToDTO(feedback)); // Wrap single feedback in a list
    }

    @Override
    public FeedbackResponseDTO getFeedbackById(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found"));
        return convertToDTO(feedback);
    }

    @Override
    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }

    @Override
    public List<FeedbackResponseDTO> getAllFeedbacks() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        return feedbacks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private FeedbackResponseDTO convertToDTO(Feedback feedback) {
        FeedbackResponseDTO dto = new FeedbackResponseDTO();
        dto.setId(feedback.getId());
        dto.setCustomerId(feedback.getCustomer().getId());
        dto.setMessage(feedback.getMessage());
        dto.setRating(feedback.getRating());
        dto.setTimestamp(feedback.getTimestamp());
        return dto;
    }

}
