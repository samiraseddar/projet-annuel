package esgi.codelink.service.scriptAndFile.executor;

import esgi.codelink.entity.User;
import esgi.codelink.entity.script.File;

import java.io.IOException;
import java.util.List;

public interface ScriptExecutor {
    String executeScript(String scriptPath, List<File> inputFiles, List<String> outputFiles, User user) throws RuntimeException, IOException;
}
