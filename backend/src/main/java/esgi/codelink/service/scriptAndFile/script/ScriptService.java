package esgi.codelink.service.scriptAndFile.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import esgi.codelink.entity.User;
import esgi.codelink.entity.script.File;
import esgi.codelink.entity.script.Script;
import esgi.codelink.enumeration.ProtectionLevel;
import esgi.codelink.repository.ScriptRepository;
import esgi.codelink.service.scriptAndFile.executor.ScriptExecutor;
import esgi.codelink.service.scriptAndFile.executor.ScriptExecutorFactory;
import esgi.codelink.service.scriptAndFile.file.FileService;
import esgi.codelink.dto.script.ScriptDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScriptService {

    private static final Logger logger = LoggerFactory.getLogger(ScriptService.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ScriptRepository scriptRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private ScriptExecutorFactory scriptExecutorFactory;

    private static final Path SCRIPTS_DIR = Paths.get("../scripts");

    public ScriptDTO saveScript(ScriptDTO scriptDTO, String scriptContent, User user) throws IOException {
        Script script = new Script(scriptDTO, user);
        makeScriptLocation(script);
        storeScriptFile(script, scriptContent);
        scriptRepository.save(script);
        return convertToDTO(script);
    }

    public ScriptDTO updateScript(ScriptDTO scriptDTO, String scriptContent, User user) throws IOException {
        Script existingScript = scriptRepository.findById(scriptDTO.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Script not found"));

        existingScript.setName(scriptDTO.getName());
        existingScript.setProtectionLevel(ProtectionLevel.valueOf(scriptDTO.getProtectionLevel()));
        existingScript.setLanguage(scriptDTO.getLanguage());
        existingScript.setInputFileExtensions(scriptDTO.getInputFileExtensions());
        existingScript.setOutputFileNames(scriptDTO.getOutputFileNames());

        makeScriptLocation(existingScript);
        storeScriptFile(existingScript, scriptContent);
        scriptRepository.save(existingScript);
        return convertToDTO(existingScript);
    }

    public void deleteScript(Long id, User user) throws IOException {
        Script script = scriptRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Script not found"));

        scriptRepository.delete(script);
        deleteScriptFile(script);
    }

    public ScriptDTO getScriptById(Long id) {
        Script script = scriptRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Script not found"));
        return convertToDTO(script);
    }

    public List<ScriptDTO> getAllScriptsByUser(User user) {
        return scriptRepository.findByUserOrProtectionLevel(user, ProtectionLevel.PUBLIC).stream()
                .peek(System.out::println)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ScriptDTO convertToDTO(Script script) {
        ScriptDTO scriptDTO = new ScriptDTO(script.getUser().getUserId());
        scriptDTO.setId(script.getScript_id());
        scriptDTO.setName(script.getName());
        scriptDTO.setLocation(script.getLocation());
        scriptDTO.setProtectionLevel(script.getProtectionLevel().toString());
        scriptDTO.setLanguage(script.getLanguage());
        scriptDTO.setInputFileExtensions(script.getInputFileExtensions());
        scriptDTO.setOutputFileNames(script.getOutputFileNames());
        return scriptDTO;
    }

    private void makeScriptLocation(Script script) {
        String complement = "/" + script.getUser().getUserId() + "/";
        Path scriptDir;
        switch (script.getLanguage().toLowerCase()) {
            case "python":
                complement = "/python" + complement;
                scriptDir = SCRIPTS_DIR.resolve(complement).normalize();
                script.setLocation(scriptDir.resolve(script.getName() + ".py").toString());
                break;
            case "javascript":
                complement = "/javascript" + complement;
                scriptDir = SCRIPTS_DIR.resolve(complement).normalize();
                script.setLocation(scriptDir.resolve(script.getName() + ".js").toString());
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported script language");
        }
    }

    private void storeScriptFile(Script script, String scriptContent) throws IOException {
        Path scriptPath = Paths.get(script.getLocation()).normalize();
        Files.createDirectories(scriptPath.getParent());
        Files.write(scriptPath, scriptContent.getBytes());
    }

    private void deleteScriptFile(Script script) throws IOException {
        Path scriptPath = Paths.get(script.getLocation()).normalize();
        Files.deleteIfExists(scriptPath);
    }

    public String executePipeline(Long initialScriptId, User user, Map<Long, List<Long>> scriptToFileMap) throws IOException, InterruptedException {
        Map<Long, String> executionResults = new LinkedHashMap<>();
        List<File> previousOutputFiles = new ArrayList<>();

        // Notifier le début du pipeline
        messagingTemplate.convertAndSend("/topic/progress", "Pipeline started for user: " + user.getFirstName());

        for (Map.Entry<Long, List<Long>> entry : scriptToFileMap.entrySet()) {
            Long scriptId = entry.getKey();
            List<Long> inputFileIds = entry.getValue();

            Script script = scriptRepository.findById(scriptId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Script not found"));

            logger.info("Executing script: {} with ID: {}", script.getName(), scriptId);

            // Notifier l'utilisateur que le script commence à s'exécuter
            messagingTemplate.convertAndSend("/topic/progress", "Executing script: " + script.getName() + " (ID: " + scriptId + ")");

            // Récupération des fichiers d'entrée
            List<File> inputFiles = new ArrayList<>();
            if (inputFileIds != null) {
                inputFiles.addAll(inputFileIds.stream().map(fileService::findById).collect(Collectors.toList()));
            }
            inputFiles.addAll(previousOutputFiles);

            for (File file : inputFiles) {
                logger.info("Input file: {} at location {}", file.getName(), file.getLocation());
                // Notifier l'utilisateur des fichiers d'entrée utilisés
                messagingTemplate.convertAndSend("/topic/progress", "Processing input file: " + file.getName() + " located at: " + file.getLocation());
            }

            // Préparer les fichiers de sortie
            List<File> outputFiles = prepareOutputFiles(script, user);

            // Exécuter le script
            ScriptExecutor executor = scriptExecutorFactory.getExecutor(script.getLanguage());
            String result = executor.executeScript(script.getLocation(), inputFiles, outputFiles);
            logger.info("Script " + script.getName() + " execution result: {}", result);

            // Notifier l'utilisateur du résultat de l'exécution
            messagingTemplate.convertAndSend("/topic/progress", "Execution result for script: " + script.getName() + " => " + result);

            // Gérer les fichiers de sortie
            for (File outputFile : outputFiles) {
                java.io.File file = new java.io.File(outputFile.getLocation());
                if (file.exists()) {
                    String content = new String(Files.readAllBytes(file.toPath()));
                    fileService.saveFile(outputFile, content, true, user);
                    logger.info("Saved output file: {} with content: {}", outputFile.getName(), content);

                    // Notifier l'utilisateur du fichier de sortie sauvegardé
                    messagingTemplate.convertAndSend("/topic/progress", "Output file saved: " + outputFile.getName());
                } else {
                    // Notifier l'utilisateur si une erreur de fichier se produit
                    String errorMessage = "File operation error: " + outputFile.getLocation();
                    messagingTemplate.convertAndSend("/topic/progress", errorMessage);
                    throw new RuntimeException(errorMessage);
                }
            }

            previousOutputFiles.clear();
            previousOutputFiles.addAll(outputFiles);

            // Ajouter le résultat de l'exécution du script dans le résultat final
            executionResults.put(scriptId, result);
        }

        // Notifier la fin du pipeline
        messagingTemplate.convertAndSend("/topic/progress", "Pipeline finished for user: " + user.getFirstName());

        // Retourner les résultats des exécutions
        return executionResults.entrySet().stream()
                .map(entry -> "Script ID: " + entry.getKey() + "\nResult:\n" + entry.getValue())
                .collect(Collectors.joining("\n\n"));
    }

    private List<File> prepareOutputFiles(Script script, User user) {
        List<String> outputFileNames = Arrays.asList(script.getOutputFileNames().split(","));
        return outputFileNames.stream()
                .map(name -> {
                    Path outputPath = fileService.getFilesDir().resolve(user.getUserId() + "/output").resolve(name).normalize();
                    try {
                        Files.createDirectories(outputPath.getParent());
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to create directories for output file: " + outputPath, e);
                    }
                    return new File(name, outputPath.toString(), true, user);
                })
                .collect(Collectors.toList());
    }
}
