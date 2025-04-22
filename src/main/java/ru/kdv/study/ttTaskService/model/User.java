package ru.kdv.study.ttTaskService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String username;
    private Role role;
}
