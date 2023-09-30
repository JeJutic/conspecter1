package pan.artem.conspecter.domain;

import pan.artem.conspecter.dto.TaskDto;

public interface SolutionIdentifier {

    TaskScore identify(TaskDto task, String solution);
}
