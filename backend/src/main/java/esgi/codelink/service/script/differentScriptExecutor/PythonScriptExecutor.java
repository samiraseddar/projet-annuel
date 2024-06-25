package esgi.codelink.service.script.differentScriptExecutor;

import esgi.codelink.service.script.ScriptExecutor;

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
    public String executeScript(String scriptPath) throws RuntimeException {
        // Ajoutez ici une logique pour vérifier le contenu du fichier de script si nécessaire
        return runProcess(new ProcessBuilder("python", scriptPath));
    }

    @Override
    public String executeRawScript(String fullScript) {
        // Vérifiez le contenu du script pour des commandes dangereuses
        if (DANGEROUS_COMMANDS.matcher(fullScript).find()) {
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
