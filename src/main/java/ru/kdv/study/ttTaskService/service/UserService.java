package ru.kdv.study.ttTaskService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.kdv.study.ttTaskService.exception.ExternalServiceException;
import ru.kdv.study.ttTaskService.config.UserServiceProperties;
import ru.kdv.study.ttTaskService.model.User;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final RestTemplate restTemplate;
    private final UserServiceProperties userServiceProperties;

    private static final String FIND_USER_URL = "/user?ids={ids}";
    private static final String GET_USER_BY_ID = "/user/{id}";
    private static final String FIND_USERS_BY_TEAM = "/member?teamId={teamId}";

    public Set<Long> getUsersByIds(final Set<Long> ids) {
        try {
            String idsString = ids.stream().map(String::valueOf).collect(Collectors.joining(","));

            return Arrays.stream(
                        Objects.requireNonNull(
                                restTemplate.getForObject(
                                        userServiceProperties.getBaseUrl() + FIND_USER_URL,
                                        User[].class,
                                        idsString
                                )
                        )
                    )
                    .map(User::getId)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw ExternalServiceException.create(String.format("Ошибка внешнего сервиса users: \n %s", e.getMessage()));
        }
    }

    public Set<User> findUsersByTeam(Long teamId) {
        try {
            return Arrays.stream(
                    Objects.requireNonNull(
                            restTemplate.getForObject(
                                    userServiceProperties.getBaseUrl() + FIND_USERS_BY_TEAM,
                                    User[].class,
                                    teamId)))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
        throw ExternalServiceException.create(String.format("Ошибка внешнего сервиса users: \n %s", e.getMessage()));}
    }

    public User getUserById(Long id) {
        try {
            return restTemplate.getForObject(userServiceProperties.getBaseUrl() + GET_USER_BY_ID, User.class, id);
        } catch (Exception e) {
            throw ExternalServiceException.create(String.format("Ошибка внешнего сервиса users: \n %s", e.getMessage()));
        }
    }
}