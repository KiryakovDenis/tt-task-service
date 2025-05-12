package ru.kdv.study.ttTaskService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class Report {
    private Integer countTask;
    private Integer toDoCount;
    private Integer inProgresCount;
    private Integer doneCount;
    private Integer deletedCount;
    private Long avgCountExecuteTask;
    @Setter
    private List<String> TopUser;
}
