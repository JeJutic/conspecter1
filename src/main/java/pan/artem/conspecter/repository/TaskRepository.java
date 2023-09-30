package pan.artem.conspecter.repository;

import pan.artem.conspecter.dto.TaskDto;

import java.util.List;

public interface TaskRepository {

    List<TaskDto> findUnsolved(int conspectId, String username);
}
