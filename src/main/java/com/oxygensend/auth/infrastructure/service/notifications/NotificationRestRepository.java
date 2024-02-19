package com.oxygensend.auth.infrastructure.service.notifications;

import com.oxygensend.auth.context.SendMailCommand;
import com.oxygensend.auth.domain.NotificationRepository;
import com.oxygensend.auth.infrastructure.service.notifications.dto.MailRequest;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NotificationRestRepository implements NotificationRepository {

    private final NotificationsClient notificationsClient;
    private final String login;
    private final String serviceId;

    @Override
    public void sendMail(SendMailCommand sendMailCommand) {
        var request = buildMailRequest(sendMailCommand);
        notificationsClient.sendMailAsync(request);
    }

    private MailRequest buildMailRequest(SendMailCommand sendMailCommand) {
        var user = sendMailCommand.user();
        var recipient = new MailRequest.EmailDto(user.email(), user.id().toString());
        var content = new MailRequest.Content(sendMailCommand.subject(), sendMailCommand.message(), List.of(recipient));
        return MailRequest.builder()
                          .login(login)
                          .serviceId(serviceId)
                          .createdAt(LocalDateTime.now())
                          .content(content)
                          .build();
    }
}
