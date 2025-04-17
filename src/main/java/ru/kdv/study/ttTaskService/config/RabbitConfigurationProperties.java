package ru.kdv.study.ttTaskService.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="app.rabbitmq")
@Getter
@Setter
public class RabbitConfigurationProperties {
    private String queue;
    private String exchange;
}
