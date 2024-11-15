package esgi.codelink.service.pipeline;

import esgi.codelink.entity.script.Script;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class PythonExecutor implements Executor {

    public File execute(Path scriptsDir, File inputFile, Script script, File jobOutputDir) {
        Path scriptPath = scriptsDir.resolve(script.getLocation()).normalize();

        if (!jobOutputDir.exists()) {
            jobOutputDir.mkdirs();
        }

        String[] command = {
                "python",
                scriptPath.toAbsolutePath().toString(),
                inputFile.getAbsolutePath(),
                jobOutputDir.getAbsolutePath()
        };

        try {
            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            System.out.println("Execution Output:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Python script execution failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to execute Python script", e);
        }

        return jobOutputDir;
    }

}
