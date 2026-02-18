package com.skill_mentor.skillmentor.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MentorDTO {
    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @Size(max = 50)
    private String title;

    @NotBlank
    @Size(max = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    private String lastName;

    private String profession;

    private String company;

    @Min(0)
    private int experienceYears;

    @Size(max = 2000)
    private String bio;

    @Pattern(regexp = "^[0-9+]{8,15}$", message = "Invalid phone number")
    private String phoneNumber;

    private Date availabilityStart;
    private Date availabilityEnd;
}
