package ru.kdv.study.ttTaskService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kdv.study.ttTaskService.exception.BadRequestException;
import ru.kdv.study.ttTaskService.model.Report;
import ru.kdv.study.ttTaskService.model.User;
import ru.kdv.study.ttTaskService.model.dto.ReportRequest;
import ru.kdv.study.ttTaskService.repository.ReportRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserService userService;

    public Report getStatisticsReport(ReportRequest reportRequest) {

        validate(reportRequest);

        /*ид пользователей входящих в команду*/
        Set<User> users = userService.findUsersByTeam(reportRequest.getTeamId());

        /*отчет по статистике пользователей команды*/
        Report report = reportRepository.getStatisticsReport(
                users.stream()
                        .map(User::getId)
                            .collect(Collectors.toSet()),
                reportRequest.getBeginDate(),
                reportRequest.getEndDate()
        );

        /*идентификаторы пользователей топ 3*/
        Set<Long> top3Ids = reportRepository.getTopWorkers(
                users.stream()
                    .map(User::getId)
                        .collect(Collectors.toSet()),
                reportRequest.getBeginDate(),
                reportRequest.getEndDate()
        );

        /*имена пользователей топ 3*/
        report.setTopUser(users.stream()
                .filter(user -> top3Ids.contains(user.getId()))
                    .map(User::getUsername)
                        .toList());

        return report;
    }

    private void validate(ReportRequest request) {
        if (request.getTeamId() == null) {
            throw BadRequestException.create("Не указан идентификатор команды");
        }

        if (request.getBeginDate().isAfter(request.getEndDate())) {
            throw BadRequestException.create("Дата начала периода должна быть меньше даты окончания периода");
        }
    }

}