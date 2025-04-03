package ru.kdv.study.ttTaskService.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExceptionMessage {
    private boolean success;
    private String message;
}