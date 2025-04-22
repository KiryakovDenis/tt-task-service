package ru.kdv.study.ttTaskService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.kdv.study.ttTaskService.exception.BadRequestException;
import ru.kdv.study.ttTaskService.model.LogOperation;
import ru.kdv.study.ttTaskService.model.Role;
import ru.kdv.study.ttTaskService.model.Status;
import ru.kdv.study.ttTaskService.model.Task;
import ru.kdv.study.ttTaskService.model.User;
import ru.kdv.study.ttTaskService.model.dto.TaskInsert;
import ru.kdv.study.ttTaskService.model.dto.TaskUpdate;
import ru.kdv.study.ttTaskService.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final LogTaskService logTaskService;
    private final TransitionService transitionService;

    @Transactional(rollbackFor = Exception.class)
    public Task create(final TaskInsert taskInsert) {
        Task tempTask = taskInsertToTask(taskInsert);
        validateData(tempTask);

        Task result = taskRepository.insert(tempTask);

        logTaskService.log(LogOperation.INSERT, result);

        return result;
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
        Task findTask = getById(taskUpdate.getId());

        Task tempTask = taskUpdateDtoToTask(taskUpdate, findTask);

        validateData(tempTask);

        if ((!findTask.getAssignee().equals(taskUpdate.getAssignee())) &&
            (taskUpdate.getAssignee() != null)) {
            validateRole(userService.getUserById(taskUpdate.getEditor()));
        }

        validateStatusTransition(findTask.getStatus(), tempTask.getStatus());

        Task result = taskRepository.updateTask(tempTask);

        logTaskService.log(LogOperation.UPDATE, result);

        return result;
    }

    @Transactional(readOnly = true)
    public boolean checkActualTaskByUser(Long id) {
        return taskRepository.checkActualTaskByUser(id);
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

    private Task taskUpdateDtoToTask(final TaskUpdate taskUpdate, Task task) {
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

    private void validateData(final Task task) {
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

    private void validateStatusTransition(Status from, Status to) {
        if ((!from.equals(to)) && (!transitionService.checkTransition(from, to))) {
            throw BadRequestException.create(String.format("Недопустимый переход состояний %s -> %s", from.name(), to.name()));
        }
    }

    private void validateRole(User user) {
        Optional.of(Role.MANAGER).ifPresentOrElse(
                r -> {
                    if (!r.equals(user.getRole())) {
                        throw BadRequestException.create(String.format("Пользователь %s не имеет прав для совершения операции",
                                user.getUsername()
                        ));
                    }
                },
                () -> {
                    throw BadRequestException.create("Для проверки роли не указана проверяемая роль");
                }
        );
    }
}