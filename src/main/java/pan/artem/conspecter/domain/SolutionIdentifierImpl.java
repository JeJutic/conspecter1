package pan.artem.conspecter.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pan.artem.conspecter.dto.TaskDto;

import java.util.Arrays;

@Component
public class SolutionIdentifierImpl implements SolutionIdentifier {

    @Override
    public TaskScore identify(TaskDto task, String solution) {
        var answer = task.getAnswer().split("\\s");
        var solved = solution.split("\\s");

        int cur = 0;
        for (int i = 0; i < Math.min(answer.length, solved.length); i++) {
            if (answer[i].equals(solved[i])) {
                cur++;
            }
        }
        return new TaskScore(cur, answer.length);
    }
}
