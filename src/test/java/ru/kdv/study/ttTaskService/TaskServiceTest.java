package ru.kdv.study.ttTaskService;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kdv.study.ttTaskService.exception.BadRequestException;
import ru.kdv.study.ttTaskService.model.Status;
import ru.kdv.study.ttTaskService.model.Task;
import ru.kdv.study.ttTaskService.model.dto.TaskInsert;
import ru.kdv.study.ttTaskService.model.dto.TaskUpdate;
import ru.kdv.study.ttTaskService.repository.TaskRepository;
import ru.kdv.study.ttTaskService.service.TaskService;
import ru.kdv.study.ttTaskService.service.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private TaskInsert insertsuccessInsertTask = new TaskInsert("test"
                                                         , "testtesttest"
                                                         , LocalDateTime.of(2025,5,5,0,0)
                                                         , 1L
                                                         , 2L
    );
    private Task insertSuccessTask = Task.builder()
            .title(insertsuccessInsertTask.getTitle())
            .description(insertsuccessInsertTask.getDescription())
            .status(Status.TO_DO)
            .deadLine(insertsuccessInsertTask.getDeadLine())
            .author(insertsuccessInsertTask.getAuthor())
            .assignee(insertsuccessInsertTask.getAssignee())
            .build();
    private TaskUpdate updateSuccessUpdateTask = new TaskUpdate(
            1L,
            "test",
            "testtesttest",
            Status.TO_DO,
            null,
            1L,
            LocalDateTime.of(2025,5,5,0,0)
    );
    private Task updateSuccessTask = Task.builder()
            .id(updateSuccessUpdateTask.getId())
            .title(updateSuccessUpdateTask.getTitle())
            .description(updateSuccessUpdateTask.getDescription())
            .status(updateSuccessUpdateTask.getStatus())
            .deadLine(updateSuccessUpdateTask.getDeadLine())
            .author(1L)
            .assignee(updateSuccessUpdateTask.getAssignee())
            .build();
    private TaskInsert NullTitleInsertTask = new TaskInsert(null
            , "testtesttest"
            , LocalDateTime.of(2025,5,5,0,0)
            , 1L
            , 2L
    );
    private TaskInsert EmptyTitleInsertTask = new TaskInsert(""
            , "testtesttest"
            , LocalDateTime.of(2025,5,5,0,0)
            , 1L
            , 2L
    );
    private TaskInsert NullAssigneeInsertTask = new TaskInsert("test"
            , "testtesttest"
            , LocalDateTime.of(2025,5,5,0,0)
            , 1L
            , null
    );
    private TaskInsert NullAuthorInsertTask = new TaskInsert("test"
            , "testtesttest"
            , LocalDateTime.of(2025,5,5,0,0)
            , null
            , 2L
    );
    private TaskInsert NullDeadLineInsertTask = new TaskInsert("test"
            , "testtesttest"
            , null
            , 1L
            , 2L
    );
    private TaskInsert ExpiredDeadLineInsertTask = new TaskInsert("test"
            , "testtesttest"
            , LocalDateTime.of(2024,5,5,0,0)
            , 1L
            , 2L
    );

    @Test
    @DisplayName("Успешное создание задачи")
    public void SuccessCreateTask() {
        Set<Long> setOfUsers =new HashSet<>(Arrays.asList(insertSuccessTask.getAssignee(),
                                                          insertSuccessTask.getAuthor()));

        Mockito.when(taskRepository.insert(insertSuccessTask)).thenReturn(insertSuccessTask);
        Mockito.when(userService.getUsersByIds(setOfUsers))
            .thenReturn(setOfUsers);

        Task result = taskService.create(insertsuccessInsertTask);

        assertThat(result).isEqualTo(insertSuccessTask);
        verify(taskRepository).insert(insertSuccessTask);
    }

    @Test
    @DisplayName("Успешное обновление задачи")
    public void SuccessUpdateTask() {
        Set<Long> setOfUsers =new HashSet<>(Arrays.asList(updateSuccessTask.getAssignee(),
                updateSuccessTask.getAuthor()));

        Mockito.when(taskRepository.getById(1L)).thenReturn(updateSuccessTask);
        Mockito.when(taskRepository.updateTask(updateSuccessTask)).thenReturn(updateSuccessTask);
        Mockito.when(userService.getUsersByIds(setOfUsers))
                .thenReturn(setOfUsers);

        Task result = taskService.update(updateSuccessUpdateTask);

        assertThat(result).isEqualTo(updateSuccessTask);
        verify(taskRepository).updateTask(updateSuccessTask);
    }

    @Test
    @DisplayName("Валидация незаполненого поля title")
    public void validateNullTitle(){
        String errorMessage = "Не заполнено поле Title.";
        BadRequestException bre = assertThrows(BadRequestException.class, () -> taskService.create(NullTitleInsertTask));
        assertThat(bre.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Валидация пустого поля title")
    public void validateEmptyTitle(){
        String errorMessage = "Не заполнено поле Title.";
        BadRequestException bre = assertThrows(BadRequestException.class, () -> taskService.create(EmptyTitleInsertTask));
        assertThat(bre.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Валидация незаполненного поля assignee")
    public void validateNullAssignee(){
        String errorMessage = "Не заполнено поле Assignee.";
        BadRequestException bre = assertThrows(BadRequestException.class, () -> taskService.create(NullAssigneeInsertTask));
        assertThat(bre.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Валидация незаполненного поля author")
    public void validateNullAuthor(){
        String errorMessage = "Не заполнено поле Author.";
        BadRequestException bre = assertThrows(BadRequestException.class, () -> taskService.create(NullAuthorInsertTask));
        assertThat(bre.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Валидация незаполненного поля deadLine")
    public void validateNullDeadLine(){
        String errorMessage = "Не заполнено поле DeadLine.";
        BadRequestException bre = assertThrows(BadRequestException.class, () -> taskService.create(NullDeadLineInsertTask));
        assertThat(bre.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Валидация просроченной даты выполнения")
    public void validateExpiredDeadLine(){
        String errorMessage = "поле DeadLine заполнено прошлой датой.";
        BadRequestException bre = assertThrows(BadRequestException.class, () -> taskService.create(ExpiredDeadLineInsertTask));

        assertThat(bre.getMessage()).isEqualTo(errorMessage);
    }
}