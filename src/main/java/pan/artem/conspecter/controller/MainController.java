package pan.artem.conspecter.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import pan.artem.conspecter.repository.ConspectRepoRepository;
import pan.artem.conspecter.repository.CurrentTaskRepository;

@AllArgsConstructor
@Controller
public class MainController {

    private final ConspectRepoRepository conspectRepoRepository;
    private final CurrentTaskRepository currentTaskRepository;

    @GetMapping("/")
    public String showMain(
            @CookieValue(value = "username", defaultValue = "") String username,
            Model model
    ) {
        if (username.isEmpty() || !username.contains("ктшник")) {   // FIXME: authentication
            return "redirect:/login";
        }
        var currentTask = currentTaskRepository.getTask(username);
        if (currentTask.isPresent()) {
            return "redirect:/task/";
        }
        var repos = conspectRepoRepository.findAll();
        model.addAttribute("repos", repos);
        return "index";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }
}
