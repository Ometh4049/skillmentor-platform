package com.skill_mentor.skillmentor.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {

    private String message;
    private String errorCode;
    private LocalDateTime timestamp;
    private Map<String, String> validationErrors;

}
