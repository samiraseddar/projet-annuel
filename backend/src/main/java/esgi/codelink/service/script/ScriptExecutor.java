package esgi.codelink.service.script;

public interface ScriptExecutor {
    String executeScript(String scriptPath) throws RuntimeException;
    String executeRawScript(String scriptContent) throws RuntimeException;
}