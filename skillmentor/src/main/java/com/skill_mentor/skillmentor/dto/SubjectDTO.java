package com.skill_mentor.skillmentor.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectDTO {

    @NotNull(message = "cannot be null")
    @Size(min = 1 , max = 20 , message = "Subject name should be less than 20 characters")
    private String subjectName;

    @Size(max = 100, message = "Description must not exceed 100 characters")
    private String description;

    @NotNull
    private Long mentorId;

}
