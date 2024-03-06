package com.oxygensend.auth.context.auth;

import com.oxygensend.auth.context.jwt.JwtFacade;
import com.oxygensend.auth.domain.AccountActivation;
import com.oxygensend.auth.domain.UserRepository;
import com.oxygensend.auth.domain.event.RegisterEvent;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class RegisterEventListenerTest {

    @Mock
    private JwtFacade jwtFacade;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RegisterEventListener listener;


    @Test
    void listen_withUnsupportedAccountActivation_shouldReturn() {
        // Arrange
        var event = new RegisterEvent(UUID.randomUUID(), "test@test.com", AccountActivation.NONE);

        // Act & Assert
        listener.listen(event);
        verifyNoInteractions(userRepository, jwtFacade);
    }


}
