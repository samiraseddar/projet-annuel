package esgi.codelink.service.scriptAndFile.executor.differentScriptExecutor;

import esgi.codelink.entity.script.File;
import esgi.codelink.entity.User;
import esgi.codelink.service.scriptAndFile.executor.ScriptExecutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PythonScriptExecutor implements ScriptExecutor {

    private static final Pattern DANGEROUS_COMMANDS = Pattern.compile(
            "\\b(rm -rf /|import os|import subprocess|exec|eval|open\\(|shutil)\\b",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public String executeScript(String scriptPath, List<File> inputFiles, List<String> outputFiles, User user) throws RuntimeException, IOException {
        if (DANGEROUS_COMMANDS.matcher(scriptPath).find()) {
            throw new RuntimeException("Le script contient des commandes dangereuses et ne peut pas être exécuté.");
        }

        List<String> inputFilePaths = inputFiles.stream().map(File::getLocation).collect(Collectors.toList());
        List<String> args = new ArrayList<>(inputFilePaths);
        args.addAll(outputFiles);

        // Ensure output directories exist
        for (String outputFile : outputFiles) {
            Path outputPath = Path.of(outputFile).normalize();
            Files.createDirectories(outputPath.getParent());
        }

        ProcessBuilder pb = new ProcessBuilder("python", scriptPath);
        pb.command().addAll(args);
        return runProcess(pb);
    }

    private String runProcess(ProcessBuilder pb) {
        try {
            Process process = pb.start();

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
}