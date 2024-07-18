package esgi.codelink.service.scriptAndFile.executor.differentScriptExecutor;

import esgi.codelink.entity.User;
import esgi.codelink.entity.script.File;
import esgi.codelink.service.scriptAndFile.executor.ScriptExecutor;
import esgi.codelink.service.scriptAndFile.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PythonScriptExecutor implements ScriptExecutor {

    @Autowired
    private FileService fileService;

    private static final Pattern DANGEROUS_COMMANDS = Pattern.compile(
            "\\b(rm -rf /|import os|import subprocess|exec|eval|shutil|system)\\b",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public String executeScript(String scriptPath, List<File> inputFiles, List<String> outputFiles, User user) throws RuntimeException, IOException {
        String scriptContent = java.nio.file.Files.readString(Path.of(scriptPath));
        if (DANGEROUS_COMMANDS.matcher(scriptContent).find()) {
            throw new RuntimeException("Le script contient des commandes dangereuses et ne peut pas être exécuté.");
        }

        List<String> inputFilePaths = inputFiles.stream().map(File::getLocation).collect(Collectors.toList());
        List<String> command = new ProcessBuilder("python", scriptPath).command();
        command.addAll(inputFilePaths);
        command.addAll(outputFiles);

        String processOutput = runProcess(new ProcessBuilder(command));

        // Move output files to the user's output directory
        for (String outputFilePath : outputFiles) {
            Path srcPath = Path.of(outputFilePath).normalize();
            Path destPath = getOutputPath(user, srcPath.getFileName().toString());
            Files.move(srcPath, destPath);
        }

        return processOutput;
    }

    private String runProcess(ProcessBuilder pb) {
        try {
            Process process = pb.start();

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = stdInput.readLine()) != null) {
                output.append(line).append("\n");
            }
            while ((line = stdError.readLine()) != null) {
                output.append("ERROR: ").append(line).append("\n");
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("Erreur lors de l'exécution du script. Code de sortie : " + exitCode + "\n" + output);
            }

            return output.toString();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Erreur lors de l'exécution du script", e);
        }
    }

    private Path getOutputPath(User user, String outputFileName) {
        // Define the user's output directory here
        return Path.of("..", "script", String.valueOf(user.getUserId()), "output", outputFileName).normalize();
    }
}