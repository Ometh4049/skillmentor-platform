package com.stemlink.skillmentor.dto.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MentorProfileResponseDTO {
    private Long id;
    private String mentorId;
    private String firstName;
    private String lastName;
    private String email;
    private String title;
    private String profession;
    private String company;
    private int experienceYears;
    private String bio;
    private String profileImageUrl;
    private Integer positiveReviews;
    private Integer totalEnrollments;
    private Boolean isCertified;
    private String startYear;

    private long subjectsCount;
    private long sessionsCount;
    private long reviewsCount;
    private Double averageRating;

    private List<MentorSubjectProfileResponseDTO> subjects = new ArrayList<>();
    private List<MentorReviewResponseDTO> reviews = new ArrayList<>();
}

