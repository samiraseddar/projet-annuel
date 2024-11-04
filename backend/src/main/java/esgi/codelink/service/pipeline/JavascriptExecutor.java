package esgi.codelink.service.pipeline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import esgi.codelink.entity.script.Script;

public class JavascriptExecutor implements Executor {

    @Override
    public File execute(Path scriptsDir, File inputFile, Script script, File jobOutputDir) {
        Path scriptPath = scriptsDir.resolve(script.getLocation()).normalize();

        File outputDir = new File("outputFiles");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        File outputFile = new File(outputDir, "output_" + script.getScript_id() + "_" + System.currentTimeMillis() + ".txt");

        String command = String.format("node %s < %s > %s",
                scriptPath.toAbsolutePath().toString(),
                inputFile.getAbsolutePath(),
                outputFile.getAbsolutePath());

        try {
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("JavaScript script execution failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to execute JavaScript script", e);
        }

        return outputFile.exists() ? outputFile : null;
    }
}
