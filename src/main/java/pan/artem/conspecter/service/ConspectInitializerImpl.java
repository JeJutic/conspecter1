package pan.artem.conspecter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pan.artem.conspecter.domain.Task;
import pan.artem.conspecter.domain.TaskMakerImpl;
import pan.artem.conspecter.domain.TaskSaver;
import pan.artem.conspecter.parser.LatexReader;
import pan.artem.conspecter.parser.ParsingProperties;
import pan.artem.conspecter.repository.TaskRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Service
public class ConspectInitializerImpl implements ConspectInitializer {

    @Value("${conspecter.basePath}")
    private String basePath;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final TaskRepository taskRepository;

    public ConspectInitializerImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    private int countCommandEntrances(String line, String command) {
        int ans = 0;

        for (int i = 0; i <= line.length() - command.length(); i++) {
            int j = 0;
            for (; j < command.length(); j++) {
                if (line.charAt(i + j) != command.charAt(j)) {
                    break;
                }
            }
            i += j;
            if (j == command.length()) {
                ans++;
            }
        }

        return ans;
    }

    public String getLastN(Deque<String> lines, int n) {
        List<String> removed = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            removed.add(lines.removeLast());
        }

        StringBuilder res = new StringBuilder();
        Collections.reverse(removed);
        for (var line : removed) {
            lines.addLast(line);
            res.append(line).append('\n');
        }
        return res.toString();
    }

    public void removeFirstN(Deque<String> lines, int n) {  // TODO
        for (int i = 0; i < n; i++) {
            lines.removeFirst();
        }
    }

    @Override
    public void initialize(int conspectId, String path) throws ParseException, IOException {
        logger.info("Initializing conspect {} with path {}", conspectId, path);

        LatexReader latexReader = new LatexReader(new BufferedReader(new FileReader(basePath + path)));
        StringBuilder headers = new StringBuilder();
        String line;
        while (true) {
            line = latexReader.readLine().get();    // FIXME
            if (countCommandEntrances(line, "\\begin{document}") == 1) {
                break;
            }
            headers.append(line).append('\n');
            if (headers.length() > ParsingProperties.getMaxLineLength()) {
                throw new ParseException("Too many headers " + headers, headers.length());
            }
        }
        TaskSaver taskSaver = new TaskSaver(headers.toString());
        Deque<String> lines = new LinkedList<>();
        lines.add(line);
        Deque<Integer> nested = new LinkedList<>();
        int cur = 0;
        int charSum = 0;
        nested.add(cur);
        while (true) {
            cur++;
            var optional = latexReader.readLine();
            if (optional.isEmpty()) {
                break;
            }
            line = optional.get();
            lines.addLast(line);
            charSum += line.length();

            int beginCount = countCommandEntrances(line, "\\begin");
            int endCount = countCommandEntrances(line, "\\end");
            if (beginCount + endCount > 1) {
                throw new ParseException(
                        "Not allowed to have multiple \\begin or \\end commands on the same line: " + line,
                        0
                );
            }

            if (beginCount == 1) {
                nested.addLast(cur);
            } else if (endCount == 1) {
                int st = nested.removeLast();
                if (charSum > ParsingProperties.getMinTaskSize()) {
                    String taskText = getLastN(lines, cur - st + 1);
                    List<Task> tasks = new TaskMakerImpl().makeTasks(taskText, 3);
                    for (var task : tasks) {
                        int taskId = taskRepository.create(taskText, task.answer(), conspectId);
                        taskSaver.saveTask(taskText, basePath + "tasks/" + taskId);
                        // TODO: ScriptExecutor for pdflatex
                    }
                }
            } else if (nested.isEmpty()) {
                lines.removeLast();
                charSum = 0;
            }
            logger.info("Nested tags parsed by the end of conspect parsing: {}", nested);
        }
    }
}