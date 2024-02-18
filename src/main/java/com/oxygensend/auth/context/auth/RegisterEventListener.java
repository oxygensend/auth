package com.oxygensend.auth.context.auth;

import com.oxygensend.auth.config.properties.TokenProperties;
import com.oxygensend.auth.context.SendMailCommand;
import com.oxygensend.auth.context.auth.jwt.TokenStorage;
import com.oxygensend.auth.context.auth.jwt.factory.TokenPayloadFactoryProvider;
import com.oxygensend.auth.context.auth.jwt.payload.TokenPayload;
import com.oxygensend.auth.domain.AccountActivation;
import com.oxygensend.auth.domain.NotificationRepository;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.User;
import com.oxygensend.auth.domain.UserRepository;
import com.oxygensend.auth.domain.event.RegisterEvent;
import com.oxygensend.auth.domain.exception.UserNotFoundException;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.oxygensend.auth.domain.NotificationMessages.ACTIVATE_ACCOUNT_BY_PASSWORD_CHANGE_MESSAGE;
import static com.oxygensend.auth.domain.NotificationMessages.ACTIVATE_ACCOUNT_BY_PASSWORD_CHANGE_SUBJECT;


@Slf4j
@RequiredArgsConstructor
@Component
final class RegisterEventListener {

    private final TokenPayloadFactoryProvider tokenPayloadFactory;
    private final TokenProperties tokenProperties;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final TokenStorage tokenStorage;
    private final Map<AccountActivation, AccountActivationHandler> strategies = Map.of(
            AccountActivation.VALIDATE_EMAIL, this::handleEmailValidation,
            AccountActivation.CHANGE_PASSWORD, this::handlePasswordChange
    );


    @EventListener
    public void listen(RegisterEvent event) {
        log.info("Handling register event: {}", event);
        var handler = strategies.get(event.accountActivation());
        if (handler == null) {
            log.info("Event: {} with id {} ignored by internal listener", event.name(), event.key());
            return;
        }

        handler.handle(event);
    }


    private void handlePasswordChange(RegisterEvent event) {
        var user = userRepository.findById(event.userId()).orElseThrow(() -> UserNotFoundException.withId(event.userId()));
        var token = tokenPayloadFactory.createToken(TokenType.PASSWORD_CHANGE,
                                                    new Date(System.currentTimeMillis() + tokenProperties.passwordChangeExpirationMs()),
                                                    new Date(System.currentTimeMillis()),
                                                    user);

        this.sendPasswordChangeEmail(user, token);
    }

    private void handleEmailValidation(RegisterEvent event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void sendPasswordChangeEmail(User user, TokenPayload token) {
        var tokenString = tokenStorage.generateToken(token);
        var command = new SendMailCommand(user, ACTIVATE_ACCOUNT_BY_PASSWORD_CHANGE_SUBJECT,
                                          ACTIVATE_ACCOUNT_BY_PASSWORD_CHANGE_MESSAGE.formatted(user.fullName(), tokenString));
        notificationRepository.sendMail(command);
    }


    interface AccountActivationHandler {
        void handle(RegisterEvent event);
    }

}
