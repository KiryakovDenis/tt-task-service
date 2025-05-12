package ru.kdv.study.ttTaskService.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.kdv.study.ttTaskService.model.Report;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReportMapper implements RowMapper<Report> {
    @Override
    public Report mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Report.builder()
                .countTask(rs.getInt("all_cnt"))
                .toDoCount(rs.getInt("to_do_cnt"))
                .inProgresCount(rs.getInt("in_progress_cnt"))
                .doneCount(rs.getInt("done_cnt"))
                .deletedCount(rs.getInt("delete_cnt"))
                .avgCountExecuteTask(rs.getLong("avg_time_execute_task"))
                .build();
    }
}
