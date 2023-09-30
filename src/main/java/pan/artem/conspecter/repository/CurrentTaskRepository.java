package pan.artem.conspecter.repository;

import pan.artem.conspecter.dto.TaskDto;

import java.util.Optional;

public interface CurrentTaskRepository {
    void setTask(String username, int taskId);
    Optional<TaskDto> getTask(String username);
    void closeTask(String username, boolean success);
}
