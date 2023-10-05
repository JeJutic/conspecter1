package pan.artem.conspecter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pan.artem.conspecter.domain.SolutionIdentifier;
import pan.artem.conspecter.dto.TaskDto;
import pan.artem.conspecter.repository.CurrentTaskRepository;
import pan.artem.conspecter.repository.TaskRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

@Controller
@RequestMapping("/task")
public class TaskController {

    @Value("${conspecter.basePath}")
    private String basePath;

    private final TaskRepository taskRepository;
    private final CurrentTaskRepository currentTaskRepository;
    private final SolutionIdentifier solutionIdentifier;

    public TaskController(TaskRepository taskRepository, CurrentTaskRepository currentTaskRepository, SolutionIdentifier solutionIdentifier) {
        this.taskRepository = taskRepository;
        this.currentTaskRepository = currentTaskRepository;
        this.solutionIdentifier = solutionIdentifier;
    }

    @GetMapping("/")
    public String showTask(
            @CookieValue(value = "username", defaultValue = "") String username,
            Model model
    ) {
        var currentTask = currentTaskRepository.getTask(username);
        if (currentTask.isEmpty()) {
            return "redirect:../";
        }
        model.addAttribute("task", currentTask.get());
        return "task";
    }

    @GetMapping("/{conspectId}")
    public String showTask(
            @CookieValue(value = "username", defaultValue = "") String username,
            @PathVariable("conspectId") int conspectId,
            Model model
    ) {
        TaskDto task;
        var currentTask = currentTaskRepository.getTask(username);
        if (currentTask.isEmpty()) {
            var tasks = taskRepository.findUnsolved(conspectId, username);
            if (tasks.isEmpty()) {
                return "redirect:../";
            }
            task = tasks.get(
                    new Random().nextInt(tasks.size())
            );
            currentTaskRepository.setTask(username, task.getId());
        } else {
            task = currentTask.get();
        }
        model.addAttribute("task", task);
        return "task";
    }

    @GetMapping("/download/{taskId}")
    public ResponseEntity<Resource> download(@PathVariable int taskId) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=task.pdf");
        File file = new File(basePath + "tasks/" + taskId + ".pdf");
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping("/**")
    public String showResult(
            @CookieValue(value = "username", defaultValue = "") String username,
            @RequestParam("solution") String solution,
            Model model
    ) {
        var currentTask = currentTaskRepository.getTask(username);
        if (currentTask.isEmpty()) {
            return "redirect:../";
        }
        var score = solutionIdentifier.identify(currentTask.get(), solution);
        String status = "";
        if ((double) score.score() / score.outOf() > 0.7) {
            currentTaskRepository.closeTask(username, true);
            status = "Solved!";
        } else {
            currentTaskRepository.closeTask(username, false);
        }

        model.addAttribute("task", currentTask.get());
        model.addAttribute("solution", solution);
        model.addAttribute("score", score);
        model.addAttribute("status", status);

        return "task_result";
    }
}
