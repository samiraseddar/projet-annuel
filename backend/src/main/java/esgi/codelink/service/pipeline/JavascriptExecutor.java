package esgi.codelink.service.pipeline;

import esgi.codelink.entity.script.Script;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class JavascriptExecutor implements Executor {

    @Override
    public File execute(Path scriptsDir, File inputFile, Script script, File jobOutputDir) {
        if (!jobOutputDir.exists()) {
            jobOutputDir.mkdirs();
        }

        Path scriptPath = scriptsDir.resolve(script.getLocation()).normalize();

        String command = String.format("node \"%s\" \"%s\" \"%s\"",
                scriptPath.toAbsolutePath().toString(),
                inputFile.getAbsolutePath(),
                jobOutputDir.getAbsolutePath());

        try {
            Process process = Runtime.getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String s;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            while ((s = stdError.readLine()) != null) {
                System.err.println(s);
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
