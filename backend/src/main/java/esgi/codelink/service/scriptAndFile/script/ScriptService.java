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
        return scriptRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ScriptDTO convertToDTO(Script script) {
        ScriptDTO scriptDTO = new ScriptDTO();
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
        switch (script.getLanguage().toLowerCase()) {
            case "python":
                complement = "/python" + complement;
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported script language");
        }
        Path scriptDir = SCRIPTS_DIR.resolve(complement).normalize();
        script.setLocation(scriptDir.resolve(script.getName() + ".py").toString());
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

        for (Map.Entry<Long, List<Long>> entry : scriptToFileMap.entrySet()) {
            Long scriptId = entry.getKey();
            List<Long> inputFileIds = entry.getValue();

            Script script = scriptRepository.findById(scriptId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Script not found"));

            logger.info("Executing script: {} with ID: {}", script.getName(), scriptId);

            List<File> inputFiles = new ArrayList<>();
            if (inputFileIds != null) {
                inputFiles.addAll(inputFileIds.stream().map(fileService::findById).collect(Collectors.toList()));
            }
            inputFiles.addAll(previousOutputFiles);

            for (File file : inputFiles) {
                logger.info("Input file: {} at location {}", file.getName(), file.getLocation());
            }

            List<File> outputFiles = prepareOutputFiles(script, user);

            ScriptExecutor executor = scriptExecutorFactory.getExecutor(script.getLanguage());
            String result = executor.executeScript(script.getLocation(), inputFiles, outputFiles);
            logger.info("Script execution result: {}", result);

            for (File outputFile : outputFiles) {
                java.io.File file = new java.io.File(outputFile.getLocation());
                if (file.exists()) {
                    String content = new String(Files.readAllBytes(file.toPath()));
                    fileService.saveFile(outputFile, content, true, user);
                    logger.info("Saved output file: {} with content: {}", outputFile.getName(), content);
                } else {
                    throw new RuntimeException("File operation error: " + outputFile.getLocation());
                }
            }

            previousOutputFiles.clear();
            previousOutputFiles.addAll(outputFiles);

            executionResults.put(scriptId, result);
        }

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
