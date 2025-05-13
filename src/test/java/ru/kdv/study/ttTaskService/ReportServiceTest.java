package ru.kdv.study.ttTaskService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kdv.study.ttTaskService.model.Report;
import ru.kdv.study.ttTaskService.model.User;
import ru.kdv.study.ttTaskService.model.dto.ReportRequest;
import ru.kdv.study.ttTaskService.repository.ReportRepository;
import ru.kdv.study.ttTaskService.service.ReportService;
import ru.kdv.study.ttTaskService.service.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReportService reportService;

    @Test
    public void testGetStatisticsReport() {
        Long teamId = 1L;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        Set<User> users = new HashSet<>(Arrays.asList(
                User.builder().id(1L).username("User1").build(),
                User.builder().id(2L).username("User2").build(),
                User.builder().id(3L).username("User3").build()
        ));
        Set<Long> top3Ids = new HashSet<>(Arrays.asList(1L, 2L, 3L));
        Report report = Report.builder().build();

        when(userService.findUsersByTeam(teamId)).thenReturn(users);
        when(reportRepository.getStatisticsReport(
                users.stream()
                        .map(User::getId)
                        .collect(Collectors.toSet()),
                startDate,
                endDate)
        ).thenReturn(report);
        when(reportRepository.getTopWorkers(
                users.stream()
                        .map(User::getId)
                        .collect(Collectors.toSet()),
                startDate,
                endDate)
        ).thenReturn(top3Ids);

        Report result = reportService.getStatisticsReport(new ReportRequest(teamId, startDate, endDate));

        assertEquals(report, result);
        assertEquals(Arrays.asList("User1", "User2", "User3"), result.getTopUser());
    }
}