package ru.kdv.study.ttTaskService.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.kdv.study.ttTaskService.config.StateMachineServiceProperties;
import ru.kdv.study.ttTaskService.model.Status;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StateMachineService {

    private final StateMachineServiceProperties stateMachineServiceProperties;
    private final RestTemplate restTemplate;

    private final static String FIND_ALLOWED_STATES_URL = "stateMachine/allowedStates/{stateFrom}";

    public boolean checkTransition(Status statusFrom, Status statusTo) {
        return findAllowedStatus(statusFrom).contains(statusTo);
    }

    private Set<Status> findAllowedStatus(Status statusFrom) {
        return Arrays.stream(
                        Objects.requireNonNull(
                                restTemplate.getForObject(
                                        buildFullUrl(),
                                        String[].class,
                                        statusFrom.name()
                                )
                        )
                )
                .map(Status::valueOf)
                .collect(Collectors.toSet());
    }

    private String buildFullUrl() {
        return stateMachineServiceProperties.getBaseUrl() +
                FIND_ALLOWED_STATES_URL;
    }
}
