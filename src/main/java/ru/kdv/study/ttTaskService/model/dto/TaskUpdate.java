package ru.kdv.study.ttTaskService.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.kdv.study.ttTaskService.model.Status;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TaskUpdate {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Long editor;
    private Long assignee;
    private LocalDateTime deadLine;
}