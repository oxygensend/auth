package com.oxygensend.auth.domain.model.identity;

public class UserUniquenessChecker {

    private final UserRepository userRepository;

    public UserUniquenessChecker(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isUnique(User user) {
        return !(userRepository.existsByEmail(user.email()) ||
            userRepository.existsByUsername(user.username()));
    }
}

