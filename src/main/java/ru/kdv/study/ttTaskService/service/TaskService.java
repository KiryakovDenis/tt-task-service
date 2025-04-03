package ru.kdv.study.ttTaskService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.kdv.study.ttTaskService.Exception.BadRequestException;
import ru.kdv.study.ttTaskService.model.Status;
import ru.kdv.study.ttTaskService.model.Task;
import ru.kdv.study.ttTaskService.model.dto.TaskInsert;
import ru.kdv.study.ttTaskService.model.dto.TaskUpdate;
import ru.kdv.study.ttTaskService.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    @Transactional(rollbackFor = Exception.class)
    public Task create(final TaskInsert taskInsert) {
        Task tempTask = taskInsertToTask(taskInsert);
        validate(tempTask);
        return taskRepository.insert(tempTask);
    }
    @Transactional(readOnly = true)
    public Task getById(Long id) {
        return taskRepository.getById(id);
    }

    @Transactional(readOnly = true)
    public List<Task> findTask(Status status, Long assigneeId) {
        return taskRepository.findTasks(status, assigneeId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Task update(final TaskUpdate taskUpdate) {

        Task tempTask = getById(taskUpdate.getId());

        tempTask = taskUpdateToTask(taskUpdate, tempTask);

        validate(tempTask);

        return taskRepository.updateTask(tempTask);
    }

    private Task taskInsertToTask(final TaskInsert taskInsert) {
        return Task.builder()
                .title(taskInsert.getTitle())
                .description(taskInsert.getDescription())
                .status(Status.TO_DO)
                .author(taskInsert.getAuthor())
                .assignee(taskInsert.getAssignee())
                .deadLine(taskInsert.getDeadLine())
                .build();
    }

    private Task taskUpdateToTask(final TaskUpdate taskUpdate, Task task) {
        Task.TaskBuilder taskBuilder = Task.builder();
        taskBuilder.id(taskUpdate.getId());
        ofNullable(taskUpdate.getTitle()).ifPresentOrElse(taskBuilder::title, () -> taskBuilder.title(task.getTitle()));
        ofNullable(taskUpdate.getDescription()).ifPresentOrElse(taskBuilder::description, () -> taskBuilder.description(task.getDescription()));
        ofNullable(taskUpdate.getStatus()).ifPresentOrElse(taskBuilder::status, () -> taskBuilder.status(task.getStatus()));
        ofNullable(taskUpdate.getAssignee()).ifPresentOrElse(taskBuilder::assignee, () -> taskBuilder.assignee(task.getAssignee()));
        ofNullable(taskUpdate.getDeadLine()).ifPresentOrElse(taskBuilder::deadLine, () -> taskBuilder.deadLine(task.getDeadLine()));
        taskBuilder.author(task.getAuthor());
        taskBuilder.editor(task.getEditor());

        return taskBuilder.build();
    }

    private void validate(final Task task) {
        if (!StringUtils.hasText(task.getTitle())) {
            throw BadRequestException.create("Не заполнено поле Title.");
        }

        if (task.getAssignee() == null) {
            throw BadRequestException.create("Не заполнено поле Assignee.");
        }

        if (task.getAuthor() == null) {
            throw BadRequestException.create("Не заполнено поле Author.");
        }

        if (task.getDeadLine() == null) {
            throw BadRequestException.create("Не заполнено поле DeadLine.");
        }

        if (task.getDeadLine().isBefore(LocalDateTime.now())) {
            throw BadRequestException.create("поле DeadLine заполнено прошлой датой.");
        }

        checkUsersIds(new HashSet<>(Arrays.asList(task.getAssignee(),
                                                  task.getAuthor()
        )));
    }

    private void checkUsersIds(Set<Long> ids) {
        Set<Long> checkedIds = userService.getUsersByIds(ids);
        ids.removeAll(checkedIds);

        if (!ids.isEmpty()) {
            String listNotFoundId = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
            throw BadRequestException.create(String.format("Не найдены пользователи с идентификаторами: %s", listNotFoundId));
        }
    }
}