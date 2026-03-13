package com.stemlink.skillmentor.dto.response;

import lombok.Data;

@Data
public class MentorSubjectProfileResponseDTO {
    private Long id;
    private String subjectName;
    private String description;
    private String courseImageUrl;
    private long enrollmentCount;
}

