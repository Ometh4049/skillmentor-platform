package com.skill_mentor.skillmentor.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectDTO {

    @Size(min = 1 , max = 20 , message = "Subject name should be less than 20 characters")
    private String subjectName;

    private String description;

}
