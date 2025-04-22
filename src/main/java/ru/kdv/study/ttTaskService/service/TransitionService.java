package ru.kdv.study.ttTaskService.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kdv.study.ttTaskService.model.Status;
import ru.kdv.study.ttTaskService.repository.TransitionRepository;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TransitionService {

    private final TransitionRepository transitionRepository;

    public boolean checkTransition(Status statusFrom, Status statusTo) {

        return transitionRepository.findAllowedStates(statusFrom.name()).stream()
                .map(Status::valueOf)
                .collect(Collectors.toSet())
                .contains(statusTo);
    }
}