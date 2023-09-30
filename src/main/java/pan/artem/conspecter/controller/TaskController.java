package pan.artem.conspecter.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pan.artem.conspecter.domain.SolutionIdentifier;
import pan.artem.conspecter.dto.TaskDto;
import pan.artem.conspecter.repository.CurrentTaskRepository;
import pan.artem.conspecter.repository.TaskRepository;

import java.util.Random;

@AllArgsConstructor
@Controller
public class TaskController {

    private final TaskRepository taskRepository;
    private final CurrentTaskRepository currentTaskRepository;
    private final SolutionIdentifier solutionIdentifier;

    @GetMapping("/task")
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

    @GetMapping("/task/{conspectId}")
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

    @PostMapping("/task/**")
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
