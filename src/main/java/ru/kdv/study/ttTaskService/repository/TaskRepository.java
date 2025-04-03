package ru.kdv.study.ttTaskService.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.kdv.study.ttTaskService.Exception.DataBaseException;
import ru.kdv.study.ttTaskService.model.Status;
import ru.kdv.study.ttTaskService.model.Task;
import ru.kdv.study.ttTaskService.repository.mapper.TaskMapper;

import java.util.List;
import static java.util.Optional.ofNullable;

@Repository
@RequiredArgsConstructor
public class TaskRepository {

    private static final String INSERT = """
            INSERT INTO tt_tasks.task (title, description, status, dead_line, assignee, author)
            VALUES (:title, :description, :status, :dead_line, :assignee, :author)
            RETURNING *
            """;

    private static final String GET_BY_ID = """
            SELECT *
              FROM tt_tasks.task
             WHERE status != 'DELETE'
               and id = :id
            """;

    private static final String FIND_TASKS = """
            SELECT *
              FROM tt_tasks.task a
             WHERE (:assignee_id::integer IS NULL OR a.assignee = :assignee_id::integer)
               AND (:status::varchar IS NULL OR a.status = :status::varchar)
               AND (:status::varchar = 'DELETE' OR a.status != 'DELETE')
            """;

    private static final String UPDATE = """
            UPDATE tt_tasks.task
               SET title = :title
                  ,description = :description
                  ,status = :status
                  ,dead_line = :dead_line
                  ,assignee = :assignee
             WHERE id = :id
               AND status != 'DELETE'
            RETURNING *
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TaskMapper taskMapper;

    public Task insert(Task task) {
        try {
            return jdbcTemplate.queryForObject(INSERT, taskToSql(task), taskMapper);
        } catch (Exception e) {
            throw DataBaseException.create(e.getMessage());
        }
    }

    public Task getById(Long id) {
        try {
            return jdbcTemplate.queryForObject(GET_BY_ID, new MapSqlParameterSource("id", id), taskMapper);
        } catch (EmptyResultDataAccessException e) {
            throw DataBaseException.create(String.format("Задача не найдена id = %d", id));
        } catch (Exception e) {
            throw DataBaseException.create(e.getMessage());
        }
    }

    public List<Task> findTasks(Status status, Long assigneeId) {
        try {
            return jdbcTemplate.query(FIND_TASKS, taskToSql(status, assigneeId), taskMapper);
        } catch (Exception e) {
            throw DataBaseException.create(e.getMessage());
        }
    }

    public Task updateTask(Task task) {
        try {
            return jdbcTemplate.queryForObject(UPDATE, taskToSql(task), taskMapper);
        } catch (Exception e) {
            throw DataBaseException.create(e.getMessage());
        }
    }
    
    private MapSqlParameterSource taskToSql(final Task task) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        
        params.addValue("id", task.getId());
        params.addValue("title", task.getTitle());
        params.addValue("description", task.getDescription());
        params.addValue("status", task.getStatus().name());
        params.addValue("dead_line", task.getDeadLine());
        params.addValue("assignee", task.getAssignee());
        params.addValue("author", task.getAuthor());

        return params;
    }

    private MapSqlParameterSource taskToSql(Status status, Long assigneeId) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("assignee_id", assigneeId);
        ofNullable(status).ifPresentOrElse(s -> params.addValue("status", s.name()),
                                           () -> params.addValue("status", null));
        /*ofNullable(assigneeId).ifPresent(assignee -> params.addValue("assignee_id", assignee));
        ofNullable(status).ifPresent(s -> params.addValue("staus", s.name()));*/

        return params;
    }
}
