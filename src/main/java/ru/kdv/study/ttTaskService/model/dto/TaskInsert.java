package ru.kdv.study.ttTaskService.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TaskInsert {
    private String title;
    private String description;
    private LocalDateTime deadLine;
    private Long author;
    private Long assignee;
}