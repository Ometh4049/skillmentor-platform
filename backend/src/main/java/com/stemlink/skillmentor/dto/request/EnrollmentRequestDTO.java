package com.stemlink.skillmentor.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class EnrollmentRequestDTO {

    @NotNull(message = "Mentor ID cannot be null")
    private String mentorId;

    @NotNull(message = "Subject ID cannot be null")
    private Long subjectId;

    @NotNull(message = "Session date/time cannot be null")
    private Date sessionAt;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;
}

