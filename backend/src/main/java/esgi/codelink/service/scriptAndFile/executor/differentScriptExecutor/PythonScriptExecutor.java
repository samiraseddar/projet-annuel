package esgi.codelink.service.scriptAndFile.executor.differentScriptExecutor;

import esgi.codelink.entity.script.File;
import esgi.codelink.entity.User;
import esgi.codelink.entity.script.Script;
import esgi.codelink.service.scriptAndFile.executor.ScriptExecutor;
import esgi.codelink.service.scriptAndFile.file.FileService;

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
    public String executeScript(String scriptPath, List<File> inputFiles, List<File> outputFiles) throws RuntimeException {
        if (DANGEROUS_COMMANDS.matcher(scriptPath).find()) {
            throw new RuntimeException("Le script contient des commandes dangereuses et ne peut pas être exécuté.");
        }

        List<String> inputFilePaths = inputFiles.stream().map(File::getLocation).collect(Collectors.toList());
        List<String> outputFilePaths = outputFiles.stream().map(File::getLocation).collect(Collectors.toList());

        List<String> command = new ArrayList<>();
        command.add("python");
        command.add(scriptPath);
        command.addAll(inputFilePaths);
        command.addAll(outputFilePaths);

        return runProcess(new ProcessBuilder(command));
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

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errorOutput = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append("ERROR: ").append(line).append("\n");
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                return output.toString();
            } else {
                return "Erreur lors de l'exécution du script. Code de sortie : " + exitCode + "\n" + errorOutput;
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Erreur lors de l'exécution du script", e);
        }
    }
}