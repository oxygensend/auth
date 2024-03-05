package com.oxygensend.auth.context.auth;

import com.oxygensend.auth.context.SendMailCommand;
import com.oxygensend.auth.context.jwt.JwtFacade;
import com.oxygensend.auth.domain.AccountActivation;
import com.oxygensend.auth.domain.NotificationRepository;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.UserRepository;
import com.oxygensend.auth.domain.event.RegisterEvent;
import com.oxygensend.auth.domain.exception.UserNotFoundException;
import com.oxygensend.auth.helper.UserMother;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.oxygensend.auth.domain.NotificationMessages.ACTIVATE_ACCOUNT_BY_EMAIL_VERIFICATION_MESSAGE;
import static com.oxygensend.auth.domain.NotificationMessages.ACTIVATE_ACCOUNT_BY_EMAIL_VERIFICATION_SUBJECT;
import static com.oxygensend.auth.domain.NotificationMessages.ACTIVATE_ACCOUNT_BY_PASSWORD_CHANGE_MESSAGE;
import static com.oxygensend.auth.domain.NotificationMessages.ACTIVATE_ACCOUNT_BY_PASSWORD_CHANGE_SUBJECT;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterEventListenerTest {

    @Mock
    private JwtFacade jwtFacade;
    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationRepository notificationRepository;


    @InjectMocks
    private RegisterEventListener listener;


    @Test
    void listen_withUnsupportedAccountActivation_shouldReturn() {
        // Arrange
        var event = new RegisterEvent(UUID.randomUUID(), "test@test.com", LocalDateTime.now(), AccountActivation.NONE);

        // Act & Assert
        listener.listen(event);
        verifyNoInteractions(userRepository, jwtFacade, notificationRepository);
    }


}
