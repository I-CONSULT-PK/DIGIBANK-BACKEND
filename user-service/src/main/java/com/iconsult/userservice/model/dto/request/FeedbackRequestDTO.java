package com.iconsult.userservice.model.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackRequestDTO {

    private Long customerId;
    private String message;

    @Min(1)
    @Max(5)
    private int rating;

}
