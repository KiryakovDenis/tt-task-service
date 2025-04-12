package ru.kdv.study.ttTaskService.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="user-service")
@Data
public class UserServiceProperties {
    private String baseUrl;
}
