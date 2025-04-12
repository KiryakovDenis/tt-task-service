package ru.kdv.study.ttTaskService.exception;

public class ExternalServiceException extends RuntimeException {
    public static ExternalServiceException create(String message) {
        return new ExternalServiceException(message);
    }

    public ExternalServiceException(String message) {
        super(message);
    }
}
