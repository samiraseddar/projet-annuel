package esgi.codelink.service.script;

public interface ScriptExecutor {
    String executeScript(String scriptPath);
    String executeRawScript(String scriptContent);
}