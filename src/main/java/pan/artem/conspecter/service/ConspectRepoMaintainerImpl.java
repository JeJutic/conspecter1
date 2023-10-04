package pan.artem.conspecter.service;

import org.springframework.stereotype.Service;
import pan.artem.conspecter.repository.ConspectRepoRepository;
import pan.artem.conspecter.repository.ConspectsRepository;
import pan.artem.conspecter.repository.TaskRepository;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Service
public class ConspectRepoMaintainerImpl implements ConspectRepoMaintainer {

    private final ScriptExecutor scriptExecutor;
    private final ConspectRepoRepository conspectRepoRepository;
    private final ConspectsRepository conspectsRepository;
    private final TaskRepository taskRepository;
    private final ConspectInitializer conspectInitializer;

    public ConspectRepoMaintainerImpl(
            ScriptExecutor scriptExecutor,
            ConspectRepoRepository conspectRepoRepository,
            ConspectsRepository conspectsRepository,
            TaskRepository taskRepository,
            ConspectInitializer conspectInitializer
    ) {
        this.scriptExecutor = scriptExecutor;
        this.conspectRepoRepository = conspectRepoRepository;
        this.conspectsRepository = conspectsRepository;
        this.taskRepository = taskRepository;
        this.conspectInitializer = conspectInitializer;
    }

    @Override
    public void loadConspectRepo(
            String url, String fullName, String author, String pathName
    ) throws IOException, InterruptedException {
        scriptExecutor.loadConspectRepo(url, author, pathName);
        conspectRepoRepository.create(fullName, author, pathName);
    }

    @Override
    public void reloadConspectRepo(String author, String pathName) throws IOException, InterruptedException, ParseException {
        int repoId = conspectRepoRepository.getId(author, pathName);
        taskRepository.deleteTasksFromRepo(repoId);     // TODO: database commits

        List<String> paths = scriptExecutor.updateConspectFiles(author, pathName);
        for (var path : paths) {
            int conspectId = conspectsRepository.getIdOrCreate(path, repoId);
            conspectInitializer.initialize(conspectId, path);
        }

        conspectRepoRepository.removeEmpty();
    }
}
