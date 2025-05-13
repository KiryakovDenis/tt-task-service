package ru.kdv.study.ttTaskService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kdv.study.ttTaskService.model.Report;
import ru.kdv.study.ttTaskService.model.dto.ReportRequest;
import ru.kdv.study.ttTaskService.service.ReportService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
@Tag(name = "Отчеты")
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    @Operation(summary = "Получение статистической информации по команде")
    public Report getReport(@Parameter(description = "ID команды", required = true) @RequestParam Long teamId,
                            @Parameter(description = "Дата начала периода", required = true) @RequestParam LocalDateTime beginDate,
                            @Parameter(description = "Дата окончания периода", required = true) @RequestParam LocalDateTime endDate) {
        return reportService.getStatisticsReport(new ReportRequest(teamId, beginDate, endDate));
    }
}