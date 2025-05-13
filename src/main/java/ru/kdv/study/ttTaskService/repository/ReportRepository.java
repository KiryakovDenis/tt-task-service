package ru.kdv.study.ttTaskService.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.kdv.study.ttTaskService.exception.DataBaseException;
import ru.kdv.study.ttTaskService.model.Report;
import ru.kdv.study.ttTaskService.repository.mapper.ReportMapper;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class ReportRepository {

    private static final String GET_STATISTICS = """
            SELECT sum(CASE WHEN a.status = 'TO_DO' THEN 1 ELSE 0 end) AS to_do_cnt,
                   sum(CASE WHEN a.status = 'IN_PROGRESS' THEN 1 ELSE 0 end) AS in_progress_cnt,
                   sum(CASE WHEN a.status = 'DONE' THEN 1 ELSE 0 end) AS done_cnt,
                   sum(CASE WHEN a.status = 'DELETE' THEN 1 ELSE 0 end) AS delete_cnt,
                   count(1) AS all_cnt,
                   (SELECT COALESCE(avg(DATE_PART('day', a.updated_at - a.created_at) * 24 +\s
                                        DATE_PART('hour', a.updated_at - a.created_at)
                                       ), 0)
                      FROM tt_tasks.task a
                     WHERE  updated_at IS NOT NULL
                       AND a.assignee IN (:ids)
                       AND a.status = 'DONE') AS avg_time_execute_task
              FROM tt_tasks.task a
             WHERE a.assignee IN (:ids)
               AND coalesce(created_at, updated_at) between :begin_date AND :end_date""";

    private static final String GET_TOP_WORKERS = """
            WITH t as(
              SELECT a.assignee AS user_id
                    ,count(1) cnt
                FROM tt_tasks.task a
               WHERE a.assignee IN (:ids)
                 AND coalesce(created_at, updated_at) between :begin_date AND :end_date
               GROUP BY a.assignee
            )
            SELECT user_id
              FROM (SELECT user_id
                          ,d_rank
                          ,sum(CASE WHEN d_rank = 1 THEN 1 ELSE 0 end) OVER () AS first_cnt
                          ,ROW_number() OVER () AS rn
                      FROM (SELECT a.user_id
                                  ,a.cnt
                                  ,dense_rank() OVER (ORDER by a.cnt desc) AS d_rank
                              FROM t a
                            ) a1
                   ) a2
             WHERE (a2.first_cnt < 3
                    AND
                    rn <=3)
                OR (a2.d_rank = 1)
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ReportMapper reportMapper;

    public Report getStatisticsReport(Set<Long> ids, LocalDateTime beginDate, LocalDateTime endDate) {
        try {
            return jdbcTemplate.queryForObject(GET_STATISTICS, createReportParams(ids, beginDate, endDate), reportMapper);
        } catch (Exception e) {
            throw DataBaseException.create(e.getMessage());
        }
    }

    public Set<Long> getTopWorkers(Set<Long> ids, LocalDateTime beginDate, LocalDateTime endDate) {
        try {
            return new HashSet<>(jdbcTemplate.queryForList(GET_TOP_WORKERS, createReportParams(ids, beginDate, endDate), Long.class));
        } catch (Exception e) {
            throw DataBaseException.create(e.getMessage());
        }
    }

    private MapSqlParameterSource createReportParams(Set<Long> ids, LocalDateTime beginDate, LocalDateTime endDate) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", ids);
        params.addValue("begin_date", beginDate);
        params.addValue("end_date", endDate);
        return params;
    }

}