package esgi.codelink.service.scriptAndFile.executor;

import esgi.codelink.entity.User;
import esgi.codelink.entity.script.File;
import esgi.codelink.entity.script.Script;
import esgi.codelink.service.scriptAndFile.file.FileService;

import java.io.IOException;
import java.util.List;

public interface ScriptExecutor {
    public String executeScript(String scriptPath, List<File> inputFiles, List<File> outputFiles) throws RuntimeException;
}
