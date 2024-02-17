package com.oxygensend.auth.unit.context.user;

import com.oxygensend.auth.context.user.UserService;
import com.oxygensend.auth.domain.UserRepository;
import com.oxygensend.auth.domain.exception.UserNotFoundException;
import com.oxygensend.auth.helper.UserMother;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;
    @InjectMocks
    private UserService service;

    @Test
    void delete_withExistingUser_shouldDeleteUser() {
        var userId = UUID.randomUUID();
        when(repository.existsById(userId)).thenReturn(true);

        service.delete(userId);

        verify(repository).deleteById(userId);
    }

    @Test
    void delete_withNonExistingUser_shouldThrowException() {
        var userId = UUID.randomUUID();
        when(repository.existsById(userId)).thenReturn(false);

        var exception = assertThrows(UserNotFoundException.class, () -> service.delete(userId));

        assertThat(exception).hasMessage("User with %s not found".formatted(userId));
    }

    @Test
    void block_withExistingUser_shouldBlockUser() {
        var userId = UUID.randomUUID();
        var user = UserMother.getRandom();
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        service.block(userId);

        verify(repository).save(user.blocked());
    }

    @Test
    void block_withNonExistingUser_shouldThrowException() {
        var userId = UUID.randomUUID();
        when(repository.findById(userId)).thenReturn(Optional.empty());

        var exception = assertThrows(UserNotFoundException.class, () -> service.block(userId));

        assertThat(exception).hasMessage("User with %s not found".formatted(userId));
    }

    @Test
    void unblock_withExistingUser_shouldUnblockUser() {
        var userId = UUID.randomUUID();
        var user = UserMother.getRandom();
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        service.unblock(userId);

        verify(repository).save(user.unblocked());
    }

    @Test
    void unblock_withNonExistingUser_shouldThrowException() {
        var userId = UUID.randomUUID();
        when(repository.findById(userId)).thenReturn(Optional.empty());

        var exception = assertThrows(UserNotFoundException.class, () -> service.unblock(userId));

        assertThat(exception).hasMessage("User with %s not found".formatted(userId));
    }
}
