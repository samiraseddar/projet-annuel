package esgi.codelink.service.scriptAndFile.script;

import esgi.codelink.dto.script.ScriptDTO;
import esgi.codelink.entity.User;
import esgi.codelink.entity.script.File;
import esgi.codelink.entity.script.Script;
import esgi.codelink.enumeration.ProtectionLevel;
import esgi.codelink.repository.ScriptRepository;
import esgi.codelink.repository.UserRepository;
import esgi.codelink.service.pipeline.ScriptLanguage;
import esgi.codelink.service.scriptAndFile.executor.ScriptExecutor;
import esgi.codelink.service.scriptAndFile.executor.ScriptExecutorFactory;
import esgi.codelink.service.scriptAndFile.file.FileService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Slf4j
@Service
public class ScriptService {

    private static final Logger logger = LoggerFactory.getLogger(ScriptService.class);

    private SimpMessagingTemplate messagingTemplate;

    private ScriptRepository scriptRepository;

    private FileService fileService;

    private ScriptExecutorFactory scriptExecutorFactory;

    private final UserRepository userRepository;

    @Autowired
    public ScriptService(SimpMessagingTemplate messagingTemplate, ScriptRepository scriptRepository, FileService fileService, ScriptExecutorFactory scriptExecutorFactory, UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.scriptRepository = scriptRepository;
        this.fileService = fileService;
        this.scriptExecutorFactory = scriptExecutorFactory;
        this.userRepository = userRepository;
    }

    //private static final Path SCRIPTS_DIR = Paths.get("../scripts");

    Path SCRIPTS_DIR = Paths.get(System.getProperty("user.dir")).getParent().resolve("scripts");

    @Transactional
    public ScriptDTO saveScript(ScriptDTO scriptDTO, String scriptContent, User user) throws IOException {
        Script script = new Script(scriptDTO, user);
        makeScriptLocation(script);
        storeScriptFile(script, scriptContent);
        scriptRepository.save(script);
        user.incrementNbPosts();
        userRepository.save(user);
        return convertToDTO(script);
    }

    @Transactional
    public ScriptDTO updateScript(ScriptDTO scriptDTO, String scriptContent, User user) throws IOException {
        Script existingScript = scriptRepository.findById(scriptDTO.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Script not found"));

        existingScript.setName(scriptDTO.getName());
        existingScript.setProtectionLevel(ProtectionLevel.valueOf(scriptDTO.getProtectionLevel()));

        makeScriptLocation(existingScript);

        existingScript.setLanguage(ScriptLanguage.fromLocation(existingScript.getLocation()));

        storeScriptFile(existingScript, scriptContent);
        scriptRepository.save(existingScript);
        return convertToDTO(existingScript);
    }

    @Transactional
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
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public String getScriptContent(long id) {
        var optional = scriptRepository.findById(id);
        if(optional.isEmpty()) return null;
        var script = optional.get();
        String scriptContent = null;
        log.info(script.getLocation());
        try {
            Path scriptPath = Paths.get(SCRIPTS_DIR.toString(), script.getLocation()).normalize();
            scriptContent = Files.readString(scriptPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scriptContent;
    }

    private ScriptDTO convertToDTO(Script script) {
        ScriptDTO scriptDTO = new ScriptDTO(script.getUser().getUserId());
        scriptDTO.setId(script.getScript_id());
        scriptDTO.setName(script.getName());
        scriptDTO.setLocation(script.getLocation());
        scriptDTO.setProtectionLevel(script.getProtectionLevel().toString());
        scriptDTO.setLanguage(script.getLanguage().name());
        scriptDTO.setNbLikes(script.getNbLikes());
        scriptDTO.setNbDislikes(script.getNbDislikes());
        return scriptDTO;
    }


    private void makeScriptLocation(Script script) {
        String complement = script.getUser().getUserId() + "/";
        System.out.println(script.getLanguage().name().toLowerCase());
        switch (script.getLanguage().name().toLowerCase()) {
            case "python":
                complement = "python/" + complement;
                break;
            case "javascript":
                complement = "javascript/" + complement;
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported script language");
        }
        Path scriptDir = SCRIPTS_DIR.resolve(complement);
        System.out.println(scriptDir);
        try { //TEST
            if (!Files.exists(scriptDir)) {
                Files.createDirectories(scriptDir);
                System.out.println("Répertoire créé : " + scriptDir);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création du répertoire des scripts", e);
        }
        System.out.println("location : " + Paths.get(complement).resolve(script.getName() + ".py"));
        script.setLocation(Paths.get(complement).resolve(script.getName() + ".py").toString());
    }

    private void storeScriptFile(Script script, String scriptContent) throws IOException {
        Path scriptPath = SCRIPTS_DIR.resolve(script.getLocation()).normalize();
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
            log.info("[SCRIPT SERVICE] - Entry = ", entry);
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

            log.info(" outputFiles content :" + outputFiles);

            // Exécuter le script
            ScriptExecutor executor = scriptExecutorFactory.getExecutor(script.getLanguage().name());
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
    public List<ScriptDTO> getAllScripts() {
        return scriptRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private List<File> prepareOutputFiles(Script script, User user) {
        return Collections.emptyList();
    }
}
