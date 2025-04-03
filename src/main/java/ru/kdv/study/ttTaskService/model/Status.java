package ru.kdv.study.ttTaskService.model;

public enum Status {
    TO_DO ("Открыта"),
    IN_PROGRESS ("В работе"),
    DONE ("Выполнена"),
    DELETE ("Закрыта");

    private String name;

    Status(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
