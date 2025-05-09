package com.oxygensend.common.domain.model;

public abstract class DomainModelConflictException extends DomainException {
    public DomainModelConflictException(String message) {
        super(message);
    }
}
