package pan.artem.conspecter.controller.error;

import lombok.Getter;

@Getter
public class ScriptExecutionException extends MyAppException {

    private final int exitCode;

    public ScriptExecutionException(int exitCode, String message) {
        super("Exit code: " + exitCode + ", " + message);
        this.exitCode = exitCode;
    }
}
