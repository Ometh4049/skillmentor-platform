package com.stemlink.skillmentor.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MeetingLinkUpdateDTO {

    @NotBlank(message = "Meeting link is required")
    private String meetingLink;
}

