package ru.kdv.study.ttTaskService.model;

public enum LogOperation {
    I ("insert"),
    U ("update");

    private String desc;

    LogOperation(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }
}
