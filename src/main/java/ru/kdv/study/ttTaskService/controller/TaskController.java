package ru.kdv.study.ttTaskService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kdv.study.ttTaskService.model.Status;
import ru.kdv.study.ttTaskService.model.Task;
import ru.kdv.study.ttTaskService.model.dto.TaskInsert;
import ru.kdv.study.ttTaskService.model.dto.TaskUpdate;
import ru.kdv.study.ttTaskService.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
@Tag(name = "Задачи", description = "endpoint для работы с задачами")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Создание задачи")
    public Task createTask(@RequestBody final TaskInsert taskInsert) {
        return taskService.create(taskInsert);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Поиск задачи по идентификатору")
    public Task getById(@PathVariable Long id) {
        return taskService.getById(id);
    }

    @GetMapping()
    @Operation(summary = "Поиск задач по статусу и/или исполнителю")
    public List<Task> findTasks(@RequestParam(required = false) Status status,
                                @RequestParam(required = false) Long assigneeId) {
        return taskService.findTask(status, assigneeId);
    }

    @PatchMapping()
    @Operation(summary = "Обновление задачи")
    public Task updateTask(@RequestBody final TaskUpdate taskUpdate) {
        return taskService.update(taskUpdate);
    }
}
