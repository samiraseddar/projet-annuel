package esgi.codelink.service.pipeline;

import esgi.codelink.entity.script.Script;

import java.io.*;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class JavascriptExecutor implements Executor {

    @Override
    public File execute(Path scriptsDir, File inputFile, Script script, File jobOutputDir) {
        if (!jobOutputDir.exists() && !jobOutputDir.mkdirs()) {
            throw new RuntimeException("Failed to create job output directory: " + jobOutputDir.getAbsolutePath());
        }

        Path scriptPath = scriptsDir.resolve(script.getLocation()).normalize();

        String[] command = {
                "node",
                scriptPath.toAbsolutePath().toString(),
                inputFile.getAbsolutePath(),
                jobOutputDir.getAbsolutePath()
        };

        try {
            System.out.println("Executing JavaScript command: " + String.join(" ", command));

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true); // Combine stdout and stderr
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String output = reader.lines().collect(Collectors.joining("\n"));
                System.out.println("Script output: \n" + output);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("JavaScript script execution failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to execute JavaScript script", e);
        }

        return jobOutputDir;
    }
}
