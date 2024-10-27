package com.example.fiap.archburgers.domain.exception;

public class DomainArgumentException extends IllegalArgumentException {
    public DomainArgumentException(String message) {
        super(message);
    }
}
