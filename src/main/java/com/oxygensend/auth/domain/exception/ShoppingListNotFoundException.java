package com.oxygensend.auth.domain.exception;

import com.oxygensend.commons_jdk.exception.ApiException;
import java.util.UUID;

public class ShoppingListNotFoundException extends ApiException {
    public ShoppingListNotFoundException(UUID id) {
        super("Shopping list with id " + id + " not found");


    }
}
