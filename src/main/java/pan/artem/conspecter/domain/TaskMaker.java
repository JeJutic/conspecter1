package pan.artem.conspecter.domain;

import java.util.List;

public interface TaskMaker {

    List<Task> makeTasks(String text, int taskCnt);
}
