package com.oxygensend.auth.infrastructure.service.notifications;

import com.oxygensend.auth.context.SendMailCommand;
import com.oxygensend.auth.helper.UserMother;
import com.oxygensend.auth.infrastructure.service.notifications.dto.MailRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationRestRepositoryTest {

    @Mock
    private NotificationsClient notificationsClient;

    @InjectMocks
    private NotificationRestRepository notificationRestRepository;

    @Test
    void sendMail_shouldCallClient() {
        var user = UserMother.getRandom();
        SendMailCommand sendMailCommand = new SendMailCommand(user, "test", "test");
        notificationRestRepository.sendMail(sendMailCommand);
        verify(notificationsClient).sendMailAsync(any(MailRequest.class));
    }
}
