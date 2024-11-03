package esgi.codelink.service.scriptAndFile.executor.differentScriptExecutor;

import esgi.codelink.entity.script.File;
import esgi.codelink.service.scriptAndFile.executor.ScriptExecutor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class JavaScriptScriptExecutor implements ScriptExecutor {

    Path SCRIPTS_DIR = Paths.get(System.getProperty("user.dir")).getParent().resolve("scripts");
    private static final Pattern DANGEROUS_COMMANDS = Pattern.compile(
            "\\b(rm -rf /|import os|import subprocess|exec|eval|shutil|child_process|fs)\\b",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public String executeScript(String scriptPath, List<File> inputFiles, List<File> outputFiles) throws RuntimeException {
        // Vérification de sécurité pour détecter les commandes dangereuses
        if (DANGEROUS_COMMANDS.matcher(scriptPath).find()) {
            throw new RuntimeException("Le script contient des commandes dangereuses et ne peut pas être exécuté.");
        }

        List<String> inputFilePaths = inputFiles.stream().map(File::getLocation).collect(Collectors.toList());
        List<String> outputFilePaths = outputFiles.stream().map(File::getLocation).collect(Collectors.toList());

        List<String> command = new ArrayList<>();
        command.add("node");  // Utilisation de Node.js pour exécuter le script JavaScript
        command.add(Paths.get(SCRIPTS_DIR.toString(), scriptPath).normalize().toString());
        command.addAll(inputFilePaths);
        command.addAll(outputFilePaths);

        return runProcess(new ProcessBuilder(command));
    }

    private String runProcess(ProcessBuilder pb) {
        try {
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder output = new StringBuilder();
            String line;

            // Lecture de la sortie standard
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            // Lecture des erreurs
            while ((line = errorReader.readLine()) != null) {
                output.append("ERROR: ").append(line).append("\n");
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                return output.toString();
            } else {
                throw new RuntimeException("Erreur lors de l'exécution du script. Code de sortie : " + exitCode + "\n" + output);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Erreur lors de l'exécution du script", e);
        }
    }
}
