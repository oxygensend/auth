package com.oxygensend.auth.context.user;

import com.oxygensend.auth.domain.UserRepository;
import com.oxygensend.auth.domain.exception.UserNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public void delete(UUID userId) {
        if (!repository.existsById(userId)) {
            throw new UserNotFoundException("User with %s not found".formatted(userId));
        }
        repository.deleteById(userId);
    }

    public void block(UUID userId) {
        var user = repository.findById(userId)
                             .orElseThrow(() -> new UserNotFoundException("User with %s not found".formatted(userId)));

        repository.save(user.blocked());
    }

    public void unblock(UUID userId) {
        var user = repository.findById(userId)
                             .orElseThrow(() -> new UserNotFoundException("User with %s not found".formatted(userId)));

        repository.save(user.unblocked());
    }
}
