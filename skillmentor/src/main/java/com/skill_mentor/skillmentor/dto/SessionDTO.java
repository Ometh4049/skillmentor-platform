package com.skill_mentor.skillmentor.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionDTO {

    @NotNull(message = "Student ID cannot be null")
    private Integer studentId;

    @NotNull(message = "Mentor ID cannot be null")
    private Long mentorId;

    @NotNull(message = "Subject ID cannot be null")
    private Long subjectId;

    @NotNull(message = "Session date/time cannot be null")
    private LocalDateTime sessionAt;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    private String sessionStatus;

    private String meetingLink;

    private String sessionNotes;

    private String studentReview;

    @Min(value = 1, message = "Rating must be at least 1")
    private Integer studentRating;


}
