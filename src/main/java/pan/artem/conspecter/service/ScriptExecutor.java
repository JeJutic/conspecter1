package pan.artem.conspecter.service;

import pan.artem.conspecter.controller.error.ScriptExecutionException;

import java.io.IOException;
import java.util.List;

public interface ScriptExecutor {

    void loadConspectRepo(String url, String author, String pathName) throws ScriptExecutionException, IOException, InterruptedException;

    List<String> updateConspectFiles(String author, String pathName) throws ScriptExecutionException, InterruptedException, IOException;

    void generatePdf(int taskId) throws IOException, InterruptedException;
}
