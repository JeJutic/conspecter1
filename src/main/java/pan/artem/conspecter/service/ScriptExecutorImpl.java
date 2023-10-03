package pan.artem.conspecter.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pan.artem.conspecter.controller.error.ScriptExecutionException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
public class ScriptExecutorImpl implements ScriptExecutor {


    @Value("${conspecter.basePath}")
    private String basePath;

    private static final class StreamGobbler implements Runnable {
        private final Process process;
        private final Consumer<String> consumer;
        private final StringBuilder errorMessage = new StringBuilder();


        private StreamGobbler(Process process, Consumer<String> consumer) {
            this.process = process;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            try {
                var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                reader.lines().forEach(consumer);
                var errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                errorReader.lines().forEach(s -> errorMessage.append(s).append('\n'));
                reader.close();
                errorReader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public String getErrorMessage() {
            return errorMessage.toString();
        }
    }

    @Override
    public void loadConspectRepo(
            String url, String author, String pathName
    ) throws ScriptExecutionException, IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(
                new String[]{"/bin/sh", "scripts/load_repo.sh", url, author, pathName},
                null,
                new File(basePath)
        );
        ScriptExecutorImpl.StreamGobbler streamGobbler = new ScriptExecutorImpl.StreamGobbler(
                process,
                System.out::println
        );
        streamGobbler.run();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new ScriptExecutionException(exitCode, streamGobbler.getErrorMessage());
        }
    }

    @Override
    public List<String> updateConspectFiles(String author, String pathName) throws ScriptExecutionException, InterruptedException, IOException {
        List<String> result = new ArrayList<>();
        Process process = Runtime.getRuntime().exec(
                new String[]{"/bin/sh", "scripts/update_conspects.sh", author, pathName},
                null,
                new File(basePath)
        );
        ScriptExecutorImpl.StreamGobbler streamGobbler = new ScriptExecutorImpl.StreamGobbler(
                process,
                s -> {
                    result.add(s);
                    System.out.println(s);
                }
        );
        streamGobbler.run();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new ScriptExecutionException(exitCode, streamGobbler.getErrorMessage());
        }
        return result;
    }
}
