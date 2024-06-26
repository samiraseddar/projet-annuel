package esgi.codelink.service.scriptAndFile.executor;

public interface ScriptExecutor {

    boolean isScriptSafe(String scriptContent);

    String executeScript(String scriptPath) throws RuntimeException;

    String executeRawScript(String scriptContent) throws RuntimeException;
}
