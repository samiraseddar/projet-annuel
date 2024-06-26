package esgi.codelink.service.scriptAndFile.executor.differentScriptExecutor;

import esgi.codelink.service.scriptAndFile.executor.ScriptExecutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class PythonScriptExecutor implements ScriptExecutor {

    private static final Pattern DANGEROUS_COMMANDS = Pattern.compile(
            "\\b(rm -rf /|import os|import subprocess|exec|eval|open\\(|shutil)\\b",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public boolean isScriptSafe(String scriptContent) {
        return !DANGEROUS_COMMANDS.matcher(scriptContent).find();
    }

    @Override
    public String executeScript(String scriptPath) throws RuntimeException {
        return runProcess(new ProcessBuilder("python", scriptPath));
    }

    @Override
    public String executeRawScript(String fullScript) {
        if (!isScriptSafe(fullScript)) {
            throw new RuntimeException("Le script contient des commandes dangereuses et ne peut pas être exécuté.");
        }

        return runProcess(new ProcessBuilder("python").redirectInput(ProcessBuilder.Redirect.PIPE), fullScript);
    }

    private String runProcess(ProcessBuilder pb, String inputScript) {
        try {
            Process process = pb.start();

            if (inputScript != null) {
                process.getOutputStream().write(inputScript.getBytes());
                process.getOutputStream().flush();
                process.getOutputStream().close();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                return output.toString();
            } else {
                throw new RuntimeException("Erreur lors de l'exécution du script. Code de sortie : " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Erreur lors de l'exécution du script", e);
        }
    }

    private String runProcess(ProcessBuilder pb) {
        return runProcess(pb, null);
    }
}