package esgi.codelink.service.scriptAndFile.executor;

import java.io.IOException;

public interface ScriptExecutor {

    boolean isScriptSafe(String scriptContent);

    String executeScript(String scriptPath) throws RuntimeException;

    String executeRawScript(String scriptContent) throws RuntimeException;

    public String executeScriptWithFiles(String scriptPath, String inputFiles, String outputFile) throws IOException, InterruptedException;
    public String executeRawScriptWithFiles(String fullScript, String inputFiles, String outputFile) throws IOException, InterruptedException;
}
