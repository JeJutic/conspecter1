package pan.artem.conspecter.domain;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TaskSaver {

    private final String headers;

    public TaskSaver(String headers) {
        this.headers = headers;
    }

    public void saveTask(String taskText, String path) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(path));
        out.write(headers);
        out.newLine();
        out.write(taskText);
        out.close();
    }
}
