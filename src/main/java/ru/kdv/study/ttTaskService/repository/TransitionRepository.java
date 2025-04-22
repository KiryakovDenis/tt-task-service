package ru.kdv.study.ttTaskService.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.kdv.study.ttTaskService.exception.DataBaseException;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransitionRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final String FIND_ALLOW_STATES = """
            SELECT a.to_state
              FROM tt_state_machine.transition a
             WHERE a.from_state = :from_state
            """;

    public List<String> findAllowedStates(String fromState) {
        try {
            return jdbcTemplate.queryForList(FIND_ALLOW_STATES, new MapSqlParameterSource("from_state", fromState), String.class);
        } catch (Exception e) {
            throw DataBaseException.create(e.getMessage());
        }
    }
}