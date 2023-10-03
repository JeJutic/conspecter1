package pan.artem.conspecter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pan.artem.conspecter.controller.error.ErrorInfo;
import pan.artem.conspecter.controller.error.ScriptExecutionException;
import pan.artem.conspecter.service.ConspectRepoMaintainer;

import java.io.*;

@RestController
public class AdminController {

    private final ConspectRepoMaintainer conspectRepoMaintainer;

    public AdminController(ConspectRepoMaintainer conspectRepoMaintainer) {
        this.conspectRepoMaintainer = conspectRepoMaintainer;
    }

    @GetMapping("/repos/{author}/{pathName}")
    public ResponseEntity<?> loadConspectRepo(
            @RequestParam String url,
            @RequestParam String fullName,
            @PathVariable String author,
            @PathVariable String pathName
    ) throws IOException, InterruptedException {
        conspectRepoMaintainer.loadConspectRepo(url, fullName, author, pathName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/repos/{author}/{pathName}/reload")
    public ResponseEntity<?> reloadConspectRepo(
            @PathVariable String author,
            @PathVariable String pathName
    ) throws IOException, InterruptedException {
        conspectRepoMaintainer.reloadConspectRepo(author, pathName);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler({ScriptExecutionException.class, IOException.class, InterruptedException.class})
    public ResponseEntity<ErrorInfo> handleScriptExecutionException(Exception e) {
        return ResponseEntity.badRequest().body(new ErrorInfo(e.getMessage()));
    }
}
