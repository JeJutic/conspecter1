package pan.artem.conspecter.controller;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pan.artem.conspecter.repository.ConspectsRepository;

@AllArgsConstructor
@Controller
public class ConspectsController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ConspectsRepository conspectsRepository;

    @GetMapping("/repo/{repoId}")
    public String showMain(
            @CookieValue(value = "username", defaultValue = "") String username,
            @PathVariable("repoId") int repoId,
            Model model
    ) {
        if (username.isEmpty()) {
            return "redirect:/login";
        }
        var conspects = conspectsRepository.findAll(username, repoId);
        logger.info("Conspects found: {}", conspects);
        model.addAttribute("conspects", conspects);
        return "conspects";
    }
}
