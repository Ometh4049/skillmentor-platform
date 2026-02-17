package com.skill_mentor.skillmentor.controllers;

import com.skill_mentor.skillmentor.dto.ErrorResponse;
import com.skill_mentor.skillmentor.exception.SkillMentorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractController {

    // Generic Exception

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex) {

        log.error("Unexpected error occurred", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("An unexpected error occurred")
                .errorCode("INTERNAL_SERVER_ERROR")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    // Custom SkillMentor Exception

    @ExceptionHandler(SkillMentorException.class)
    public ResponseEntity<ErrorResponse> handleSkillMentorException(
            SkillMentorException ex) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode(ex.getStatus().name())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(
                errorResponse,
                ex.getStatus()
        );
    }

    // Validation Exception

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Validation failed")
                .errorCode(HttpStatus.BAD_REQUEST.name())
                .timestamp(LocalDateTime.now())
                .validationErrors(errors)
                .build();

        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.BAD_REQUEST
        );
    }
}