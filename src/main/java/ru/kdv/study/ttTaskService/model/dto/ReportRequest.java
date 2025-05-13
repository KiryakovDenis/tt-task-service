package ru.kdv.study.ttTaskService.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReportRequest {
    private Long teamId;
    private LocalDateTime beginDate;
    private LocalDateTime endDate;
}
