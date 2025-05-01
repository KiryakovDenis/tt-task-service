package ru.kdv.study.ttTaskService.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.kdv.study.ttTaskService.config.RabbitConfigurationProperties;
import ru.kdv.study.ttTaskService.model.LogOperation;
import ru.kdv.study.ttTaskService.model.Task;
import ru.kdv.study.ttTaskService.model.dto.LogLine;

import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogTaskService {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitConfigurationProperties rabbitConfigurationProperties;

    public void log(LogOperation logOperation, Task task) {
        try {
            rabbitTemplate.convertAndSend(
                    rabbitConfigurationProperties.getExchange(),
                    rabbitConfigurationProperties.getQueue(),
                    new LogLine(logOperation, task)
            );
        } catch (Exception e) {
            log.error(new StringJoiner("/n")
                    .add("Ошибка при попытке логирования изменений данных о задаче")
                    .add(task.toString())
                    .add(e.getMessage())
                    .toString());
        }
    }
}