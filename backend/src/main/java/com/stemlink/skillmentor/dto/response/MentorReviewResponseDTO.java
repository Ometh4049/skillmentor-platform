package com.stemlink.skillmentor.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class MentorReviewResponseDTO {
    private Integer sessionId;
    private String studentName;
    private Integer rating;
    private String review;
    private Date sessionAt;
}

