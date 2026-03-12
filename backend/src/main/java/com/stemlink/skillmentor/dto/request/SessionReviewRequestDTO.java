package com.stemlink.skillmentor.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SessionReviewRequestDTO {

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer studentRating;

    @NotBlank(message = "Review text is required")
    private String studentReview;
}

