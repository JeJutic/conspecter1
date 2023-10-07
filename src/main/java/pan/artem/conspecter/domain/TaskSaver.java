package pan.artem.conspecter.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TaskSaver {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String headers;

    public TaskSaver(String headers) {
        this.headers = headers;
    }

    public void saveTask(String taskText, String path) throws IOException {
        logger.info("Saving task with on {}", path);
        BufferedWriter out = new BufferedWriter(new FileWriter(path, StandardCharsets.UTF_8));
        out.write(headers);
        out.newLine();
        out.write(taskText);
        out.newLine();
        out.write("\\end{document}\n");
        out.close();
        logger.info("Task saved");
    }
}
