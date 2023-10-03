package pan.artem.conspecter.repository;

import pan.artem.conspecter.dto.TaskDto;

import java.util.List;

public interface TaskRepository {

    void create(String text, String answer, int conspectId);

    List<TaskDto> findUnsolved(int conspectId, String username);

    void deleteTasksFromRepo(int repoId);
}
