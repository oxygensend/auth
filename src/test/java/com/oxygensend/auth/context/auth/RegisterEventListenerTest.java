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

    @Test
    void listen_withUserNotFound_shouldThrowException() {
        // Arrange
        var event = new RegisterEvent(UUID.randomUUID(), "test@test.com", LocalDateTime.now(), AccountActivation.CHANGE_PASSWORD);

        when(userRepository.findById(event.userId())).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> listener.listen(event));
    }

    @Test
    void listen_withPasswordChangeAccountActivation_shouldHandlePasswordChange() {
        // Arrange
        var event = new RegisterEvent(UUID.randomUUID(), "test@test.com", LocalDateTime.now(), AccountActivation.CHANGE_PASSWORD);
        var token = RandomStringUtils.randomAlphabetic(20);
        var user = UserMother.getRandom();

        var command = new SendMailCommand(user, ACTIVATE_ACCOUNT_BY_PASSWORD_CHANGE_SUBJECT,
                                          ACTIVATE_ACCOUNT_BY_PASSWORD_CHANGE_MESSAGE.formatted(user.fullName(), token));

        when(userRepository.findById(event.userId())).thenReturn(Optional.of(user));
        when(jwtFacade.generateToken(eq(user), eq(TokenType.PASSWORD_RESET))).thenReturn(token);

        // Act & Assert
        listener.listen(event);

        // Assert
        verify(notificationRepository, times(1)).sendMail(command);

    }

    @Test
    void listen_withEmailVerificationAccountActivation_shouldHandleEmailVerification() {
        // Arrange
        var event = new RegisterEvent(UUID.randomUUID(), "test@test.com", LocalDateTime.now(), AccountActivation.VERIFY_EMAIL);
        var token = RandomStringUtils.randomAlphabetic(20);
        var user = UserMother.getRandom();

        var command = new SendMailCommand(user, ACTIVATE_ACCOUNT_BY_EMAIL_VERIFICATION_SUBJECT,
                                          ACTIVATE_ACCOUNT_BY_EMAIL_VERIFICATION_MESSAGE.formatted(user.fullName(), token));

        when(userRepository.findById(event.userId())).thenReturn(Optional.of(user));
        when(jwtFacade.generateToken(eq(user), eq(TokenType.EMAIL_VERIFICATION))).thenReturn(token);

        // Act & Assert
        listener.listen(event);

        // Assert
        verify(notificationRepository, times(1)).sendMail(command);

    }

}
