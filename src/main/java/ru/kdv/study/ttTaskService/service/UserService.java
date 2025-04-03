package ru.kdv.study.ttTaskService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.kdv.study.ttTaskService.Exception.ExternalServiceException;
import ru.kdv.study.ttTaskService.config.UserServiceProperties;
import ru.kdv.study.ttTaskService.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final RestTemplate restTemplate;
    private final UserServiceProperties userServiceProperties;

    private static final String USER_EXIST_URL = "/user";

    public Set<Long> getUsersByIds(final Set<Long> ids) {
        User[] users;
        try {
            String fullUrl = buildUserUrl(ids);
            users = restTemplate.getForObject(fullUrl, User[].class);
        } catch (Exception e) {
            throw ExternalServiceException.create(String.format("Ошибка внешнего сервиса users: \n %s", e.getMessage()));
        }

        if (users != null) {
            return Arrays.stream(users).map(User::getId).collect(Collectors.toSet());
        } else {
            return null;
        }
    }

    private String buildUserUrl(final Set<Long> ids) {
        String listIds = ids.stream().map((s) -> {return "ids="+s;}).collect(Collectors.joining("&"));
        return userServiceProperties.getBaseUrl() +
                USER_EXIST_URL +
                "?" +
                listIds;
    }

}
