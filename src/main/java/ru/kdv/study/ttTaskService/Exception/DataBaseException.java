package ru.kdv.study.ttTaskService.Exception;

public class DataBaseException extends RuntimeException {
    public static DataBaseException create(String message) {
        return new DataBaseException(message);
    }

    public DataBaseException(String message) {
        super(message);
    }
}