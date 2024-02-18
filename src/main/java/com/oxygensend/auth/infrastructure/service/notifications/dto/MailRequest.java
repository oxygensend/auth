package com.oxygensend.auth.infrastructure.service.notifications.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record MailRequest(Content content,
                          String login,
                          String serviceId,
                          String requestId,
                          LocalDateTime createdAt) {


    public record Content(String subject, String body, List<EmailDto> recipients) {

    }

    public record EmailDto(String address, String systemId) {

    }

}
