package pan.artem.conspecter.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pan.artem.conspecter.repository.ConspectRepoRepository;
import pan.artem.conspecter.repository.ConspectsRepository;
import pan.artem.conspecter.repository.TaskRepository;

import java.io.IOException;
import java.util.List;

@Service
public class ConspectRepoMaintainerImpl implements ConspectRepoMaintainer {

    @Value("${conspecter.basePath}")
    private String basePath;

    private final ScriptExecutor scriptExecutor;
    private final ConspectRepoRepository conspectRepoRepository;
    private final ConspectsRepository conspectsRepository;
    private final TaskRepository taskRepository;

    public ConspectRepoMaintainerImpl(
            ScriptExecutor scriptExecutor,
            ConspectRepoRepository conspectRepoRepository,
            ConspectsRepository conspectsRepository,
            TaskRepository taskRepository
    ) {
        this.scriptExecutor = scriptExecutor;
        this.conspectRepoRepository = conspectRepoRepository;
        this.conspectsRepository = conspectsRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public void loadConspectRepo(
            String url, String fullName, String author, String pathName
    ) throws IOException, InterruptedException {
        scriptExecutor.loadConspectRepo(url, author, pathName);
        conspectRepoRepository.create(fullName, author, pathName);
    }

    private void initializeConspect(String path, int repoId) {
        int conspectId = conspectsRepository.getIdOrCreate(path, repoId);

    }

    @Override
    public void reloadConspectRepo(String author, String pathName) throws IOException, InterruptedException {
        int repoId = conspectRepoRepository.getId(author, pathName);
        taskRepository.deleteTasksFromRepo(repoId);     // TODO: database commits

        List<String> paths = scriptExecutor.updateConspectFiles(author, pathName);
        for (var path : paths) {
            initializeConspect(path, repoId);
        }

        conspectRepoRepository.removeEmpty();
    }
}
