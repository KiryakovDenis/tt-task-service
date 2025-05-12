package ru.kdv.study.ttTaskService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kdv.study.ttTaskService.model.Report;
import ru.kdv.study.ttTaskService.model.User;
import ru.kdv.study.ttTaskService.repository.ReportRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserService userService;

    public Report getStatisticsReport(Long teamId) {

        /*ид пользователей входящих в команду*/
        Set<User> users = userService.findUsersByTeam(teamId);

        /*отчет по статистике пользователей команды*/
        Report report = reportRepository.getStatisticsReport(users.stream()
                .map(User::getId)
                    .collect(Collectors.toSet())
        );

        /*идентификаторы пользователей топ 3*/
        Set<Long> top3Ids = reportRepository.getTopWorkers(users.stream()
                .map(User::getId)
                    .collect(Collectors.toSet())
        );

        /*имена пользователей топ 3*/
        report.setTopUser(users.stream()
                .filter(user -> top3Ids.contains(user.getId()))
                    .map(User::getUsername)
                        .toList());

        return report;
    }
}