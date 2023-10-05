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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

@Service
public class ConspectInitializerImpl implements ConspectInitializer {

    @Value("${conspecter.basePath}")
    private String basePath;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final TaskRepository taskRepository;
    private final ScriptExecutor scriptExecutor;

    public ConspectInitializerImpl(TaskRepository taskRepository, ScriptExecutor scriptExecutor) {
        this.taskRepository = taskRepository;
        this.scriptExecutor = scriptExecutor;
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

    public int removeFirstN(Deque<String> lines, int n) {  // TODO
        int remCnt = 0;
        for (int i = 0; i < n; i++) {
            String removed = lines.removeFirst();
            remCnt += removed.length();
        }
        return remCnt;
    }

    @Override
    public void initialize(int conspectId, String path) throws ParseException, IOException, InterruptedException {
        logger.info("Initializing conspect {} with path {}", conspectId, path);

        LatexReader latexReader = new LatexReader(new BufferedReader(
                        new FileReader(basePath + path, StandardCharsets.UTF_8)
        ));
        StringBuilder headers = new StringBuilder();
        String line;
        while (true) {
            line = latexReader.readLine().get();    // FIXME
            headers.append(line).append('\n');
            if (countCommandEntrances(line, "\\begin{document}") == 1) {
                break;
            }
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
//            if (Math.abs(beginCount - endCount) > 1) {
//                throw new ParseException(
//                        "Not allowed to have multiple \\begin or \\end commands on the same line: " + line,
//                        0
//                );
//            }

            for (int i = 0; i < beginCount - endCount; i++) {
                nested.addLast(cur);
            }

            if (nested.isEmpty()) {
                lines.removeLast();
                charSum = 0;
            } else {
                for (int i = 0; i < endCount - beginCount; i++) {
                    if (nested.isEmpty()) {
                        throw new ParseException("More \\end than \\begin", latexReader.linesRead());   // TODO: add info about conspectId in domain exception
                    }
                    int st = nested.removeLast();
                    if (charSum > ParsingProperties.getMinTaskSize()) {
                        String taskText = getLastN(lines, cur - st + 1);
                        List<Task> tasks = new TaskMakerImpl().makeTasks(taskText, 3);
                        for (var task : tasks) {
                            int taskId = taskRepository.create(task.text(), task.answer(), conspectId);
                            taskSaver.saveTask(task.text(), basePath + "tasks/" + taskId + ".tex");
                            scriptExecutor.generatePdf(taskId);
                            // TODO: ScriptExecutor for pdflatex
                        }
                    }
                }

                while (charSum > ParsingProperties.getMaxLineLength()) {
                    int first = nested.removeFirst();
                    if (nested.isEmpty()) {
                        lines.clear();
                        charSum = 0;
                        break;
                    }
                    charSum -= removeFirstN(lines, nested.getFirst() - first);
                }
            }
            logger.info("Nested tags parsed by the end of conspect parsing: {}", nested);
        }
        if (!nested.isEmpty()) {
            throw new ParseException("More \\begin than \\end", latexReader.linesRead());
        }
        latexReader.close();
    }
}
