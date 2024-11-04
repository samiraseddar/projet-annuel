package esgi.codelink.service.pipeline;

import esgi.codelink.entity.script.Script;

import java.io.File;
import java.nio.file.Path;

public interface Executor {

    File execute(Path scriptsDir, File inputFile, Script script, File jobOutputDir);

    static Executor getExecutorForScript(Script script) {
        return switch (script.getLanguage()) {
            case PYTHON -> new PythonExecutor();
            case JAVASCRIPT -> new JavascriptExecutor();
            default -> throw new UnsupportedOperationException("Unsupported language: " + script.getLanguage());
        };
    }
}