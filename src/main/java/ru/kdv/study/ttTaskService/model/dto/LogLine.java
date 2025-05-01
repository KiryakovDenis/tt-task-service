package ru.kdv.study.ttTaskService.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.kdv.study.ttTaskService.model.LogOperation;
import ru.kdv.study.ttTaskService.model.Task;

@Data
@AllArgsConstructor
public class LogLine {
    private LogOperation logOperation;
    private Task task;
}
