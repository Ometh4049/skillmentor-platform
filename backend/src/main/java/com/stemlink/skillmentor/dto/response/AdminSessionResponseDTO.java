package com.stemlink.skillmentor.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class AdminSessionResponseDTO {
    private Integer id;
    private String studentName;
    private String studentEmail;
    private String mentorName;
    private String mentorEmail;
    private String subjectName;
    private Date sessionAt;
    private Integer durationMinutes;
    private String paymentStatus;
    private String sessionStatus;
    private String meetingLink;
}

