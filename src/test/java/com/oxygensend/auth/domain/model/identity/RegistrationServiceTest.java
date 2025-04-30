package com.oxygensend.auth.domain.model.identity;

import com.oxygensend.auth.domain.model.identity.exception.UnexpectedRoleException;
import com.oxygensend.auth.domain.model.identity.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    private RegistrationService registrationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private Credentials credentials;

    @Mock
    private EmailAddress email;

    @Mock
    private Username username;

    @Mock
    private UserId userId;

    @Mock
    private BusinessId businessId;

    @Mock
    private User user;

    private Set<Role> roles;
    private AccountActivationType accountActivationType;

    @BeforeEach
    void setUp() {
        registrationService = new RegistrationService(userRepository, roleRepository);
        
        roles = Set.of(new Role("USER"));
        accountActivationType = AccountActivationType.VERIFY_EMAIL;
        
       lenient().when(credentials.email()).thenReturn(email);
       lenient().when(credentials.username()).thenReturn(username);
    }

    @Test
    void shouldRegisterUser() {
        // Given
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(roleRepository.exists(any(Role.class))).thenReturn(true);
        when(userRepository.nextIdentity()).thenReturn(userId);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User result = registrationService.registerUser(credentials, roles, businessId, accountActivationType);

        // Then
        assertThat(result).isEqualTo(user);
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        
        verify(userRepository).nextIdentity();
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> 
            registrationService.checkIfUserExists(credentials)
        );
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByUsername(username)).thenReturn(true);

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> 
            registrationService.checkIfUserExists(credentials)
        );
    }

    @Test
    void shouldThrowExceptionWhenRolesDoNotExist() {
        // Given
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(roleRepository.exists(any(Role.class))).thenReturn(false);

        // When & Then
        assertThrows(UnexpectedRoleException.class, () -> 
            registrationService.registerUser(credentials, roles, businessId, accountActivationType)
        );
    }
}
