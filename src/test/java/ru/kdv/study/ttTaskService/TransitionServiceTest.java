package ru.kdv.study.ttTaskService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kdv.study.ttTaskService.model.Status;
import ru.kdv.study.ttTaskService.repository.TransitionRepository;
import ru.kdv.study.ttTaskService.service.TransitionService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransitionServiceTest {

    @Mock
    private TransitionRepository transitionRepository;

    @InjectMocks
    private TransitionService transitionService;

    @Test
    public void testCheckTransition_whenTransitionAllowed() {

        Status statusFrom = Status.TO_DO;
        Status statusTo = Status.IN_PROGRESS;
        List<String> allowedStates = Arrays.asList("IN_PROGRESS", "DELETE");
        when(transitionRepository.findAllowedStates(statusFrom.name())).thenReturn(allowedStates);

        boolean result = transitionService.checkTransition(statusFrom, statusTo);

        assertTrue(result);
    }

    @Test
    public void testCheckTransition_whenTransitionNotAllowed() {

        Status statusFrom = Status.TO_DO;
        Status statusTo = Status.DONE;
        List<String> allowedStates = Arrays.asList("IN_PROGRESS", "TO_DO");
        when(transitionRepository.findAllowedStates(statusFrom.name())).thenReturn(allowedStates);

        boolean result = transitionService.checkTransition(statusFrom, statusTo);

        assertFalse(result);
    }
}