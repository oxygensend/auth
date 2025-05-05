package common.domain.model;

public abstract class DomainException extends RuntimeException{

    public DomainException(String message) {
        super(message);
    }
}
