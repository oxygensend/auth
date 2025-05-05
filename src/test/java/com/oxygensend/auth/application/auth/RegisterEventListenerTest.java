//package com.oxygensend.auth.application.auth;
//
//import com.oxygensend.auth.application.RegisterEventListener;
//import com.oxygensend.auth.application.token.TokenApplicationService;
//import com.oxygensend.auth.domain.model.loginType.AccountActivationType;
//import com.oxygensend.auth.domain.model.loginType.UserRepository;
//import com.oxygensend.auth.domain.model.loginType.event.RegisterEvent;
//import java.util.UUID;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static org.mockito.Mockito.verifyNoInteractions;
//
//@ExtendWith(MockitoExtension.class)
//class RegisterEventListenerTest {
//
//    @Mock
//    private TokenApplicationService jwtFacade;
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private RegisterEventListener listener;
//
//
//    @Test
//    void listen_withUnsupportedAccountActivation_shouldReturn() {
//        // Arrange
//        var event = new RegisterEvent(UUID.randomUUID(), "123", "test@test.com", AccountActivationType.NONE);
//
//        // Act & Assert
//        listener.listen(event);
//        verifyNoInteractions(userRepository, jwtFacade);
//    }
//
//
//}
