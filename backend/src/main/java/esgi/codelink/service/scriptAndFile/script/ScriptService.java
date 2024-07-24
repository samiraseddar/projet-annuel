package esgi.codelink.service.scriptAndFile.script;

import esgi.codelink.dto.script.ScriptDTO;
import esgi.codelink.entity.User;
import esgi.codelink.entity.script.File;
import esgi.codelink.entity.script.Script;
import esgi.codelink.enumeration.ProtectionLevel;
import esgi.codelink.repository.FileRepository;
import esgi.codelink.repository.ScriptRepository;
import esgi.codelink.service.UserService;
import esgi.codelink.service.scriptAndFile.executor.ScriptExecutor;
import esgi.codelink.service.scriptAndFile.executor.ScriptExecutorFactory;
import esgi.codelink.service.scriptAndFile.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScriptService {

    private static final Path SCRIPTS_DIR = Path.of("../script").normalize();

    @Autowired
    private UserService userService;

    @Autowired
    private ScriptRepository scriptRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileRepository fileRepository;

    public ScriptDTO saveScript(ScriptDTO scriptDTO, String scriptContent, User user) throws IOException {
        Script script = new Script(scriptDTO, user);

        script.setProtectionLevel(ProtectionLevel.valueOf(scriptDTO.getProtectionLevel()));
        script.setLanguage(scriptDTO.getLanguage());
        script.setInputFileExtensions(scriptDTO.getInputFileExtensions());
        script.setOutputFileNames(scriptDTO.getOutputFileNames());

        makeScriptLocation(script);

        Path scriptPath = Path.of(script.getLocation()).normalize();
        storeScriptFile(scriptPath, scriptContent);
        scriptRepository.save(script);
        return convertToDTO(script);
    }

    public ScriptDTO updateScript(ScriptDTO scriptDTO, String scriptContent, User user) throws IOException {
        Script script = scriptRepository.findById(scriptDTO.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Script not found"));
        script.setName(scriptDTO.getName());

        script.setProtectionLevel(ProtectionLevel.valueOf(scriptDTO.getProtectionLevel()));
        script.setLanguage(scriptDTO.getLanguage());
        script.setInputFileExtensions(scriptDTO.getInputFileExtensions());
        script.setOutputFileNames(scriptDTO.getOutputFileNames());

        makeScriptLocation(script);
        Path scriptPath = Path.of(script.getLocation()).normalize();

        storeScriptFile(scriptPath, scriptContent);
        scriptRepository.save(script);
        return convertToDTO(script);
    }

    public void deleteScript(Long id, User user) {
        Script script = scriptRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Script not found"));
        scriptRepository.delete(script);
    }

    public List<ScriptDTO> getAllScriptsByUser(User user) {
        return scriptRepository.findByUser(user).stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    public ScriptDTO getScriptById(Long id) {
        Script scriptCible = scriptRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Script not found"));


        return convertToDTO(scriptCible) ;
    }

    public String executeScript(Long id, User user, List<Long> inputFileIds, List<Long> inputScriptIds) throws IOException {
        Script script = scriptRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Script not found"));

        List<File> inputFileEntities = inputFileIds != null ? inputFileIds.stream()
                .map(this::findFileById)
                .collect(Collectors.toList()) : List.of();

        List<Script> inputScriptEntities = inputScriptIds != null ? inputScriptIds.stream()
                .map(this::findScriptById)
                .collect(Collectors.toList()) : List.of();

        for (Script inputScript : inputScriptEntities) {
            executeScript(inputScript.getScript_id(), user, inputFileIds, inputScriptIds);
        }

        ScriptExecutor executor = ScriptExecutorFactory.getExecutor(script.getLanguage());

        List<String> outputFileNames = script.getOutputFileNames() != null ? List.of(script.getOutputFileNames().split(" ")) : List.of();
        List<String> outputFilePaths = outputFileNames.stream()
                .map(fileName -> getOutputFilePath(user, fileName).toString())
                .collect(Collectors.toList());

        String result = executor.executeScript(script.getLocation(), inputFileEntities, outputFilePaths, user);

        for (String outputFilePath : outputFilePaths) {
            Path path = Path.of(outputFilePath);
            String content = java.nio.file.Files.readString(path);
            File outputFile = new File(path.getFileName().toString(), content, true, user);
            fileService.saveFile(outputFile, content, true, user);
        }

        return result;
    }

    private void saveOutputFile(File outputFile, String content) throws IOException {
        Path outputPath = Path.of(outputFile.getLocation()).normalize();
        if (Files.notExists(outputPath.getParent())) {
            Files.createDirectories(outputPath.getParent());
        }
        Files.write(outputPath, content.getBytes());

        fileService.saveFile(outputFile, content, true, outputFile.getUser());
    }


    private void makeScriptLocation(Script script) throws IOException {
        String complement = script.getUser().getUserId() + "/";
        switch (script.getLanguage().toLowerCase()) {
            case "python":
                complement = "python/" + complement;
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported script language");
        }
        Path scriptDir = SCRIPTS_DIR.resolve(complement).normalize();
        if (Files.notExists(scriptDir)) {
            Files.createDirectories(scriptDir);
        }
        script.setLocation(scriptDir.resolve(script.getName() + ".py").toString());
    }

    private void storeScriptFile(Path scriptPath, String scriptContent) throws IOException {
        Files.createDirectories(scriptPath.getParent());
        Files.write(scriptPath, scriptContent.getBytes());

        if (Files.notExists(scriptPath)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create the file at " + scriptPath);
        }
    }

    public String executePipeline(Long initialScriptId, User user, Map<Long, List<Long>> scriptToFileMap) throws IOException {
        Map<Long, List<Long>> resultMap = new HashMap<>();
        return executeScriptsRecursively(initialScriptId, user, scriptToFileMap, resultMap);
    }

    private String executeScriptsRecursively(Long scriptId, User user, Map<Long, List<Long>> scriptToFileMap, Map<Long, List<Long>> resultMap) throws IOException {
        Script script = findScriptById(scriptId);
        List<Long> inputFileIds = scriptToFileMap.getOrDefault(scriptId, Collections.emptyList());

        // Add previously generated output files to the input file list
        List<Long> previousOutputFileIds = resultMap.get(scriptId);
        if (previousOutputFileIds != null) {
            inputFileIds.addAll(previousOutputFileIds);
        }

        List<File> inputFiles = inputFileIds.stream()
                .map(this::findFileById)
                .collect(Collectors.toList());

        List<String> outputFilePaths = script.getOutputFileNames() != null
                ? List.of(script.getOutputFileNames().split(" "))
                : new ArrayList<>();

        ScriptExecutor executor = ScriptExecutorFactory.getExecutor(script.getLanguage());
        String result = executor.executeScript(script.getLocation(), inputFiles, outputFilePaths, user);

        List<Long> outputFileIds = new ArrayList<>();
        for (String outputFilePath : outputFilePaths) {
            String content = Files.readString(Path.of(outputFilePath));
            File outputFile = new File(Path.of(outputFilePath).getFileName().toString(), content, true, user);
            outputFile = fileService.saveFile(outputFile, content, true, user);
            outputFileIds.add(outputFile.getId());
        }

        resultMap.put(scriptId, outputFileIds);

        // Iterate over the next scripts to execute
        for (Map.Entry<Long, List<Long>> entry : scriptToFileMap.entrySet()) {
            if (!resultMap.containsKey(entry.getKey())) {
                scriptToFileMap.put(entry.getKey(), outputFileIds);
                executeScriptsRecursively(entry.getKey(), user, scriptToFileMap, resultMap);
            }
        }

        return result;
    }


    private ScriptDTO convertToDTO(Script script) {
        ScriptDTO dto = new ScriptDTO();
        dto.setId(script.getScript_id());
        dto.setName(script.getName());
        dto.setLocation(script.getLocation());
        dto.setProtectionLevel(script.getProtectionLevel().name());
        dto.setLanguage(script.getLanguage());
        dto.setInputFileExtensions(script.getInputFileExtensions());
        dto.setOutputFileNames(script.getOutputFileNames());
        return dto;
    }

    private File findFileById(Long id) {
        return fileService.getFileById(id);
    }

    private Script findScriptById(Long id) {
        return scriptRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Script not found"));
    }

    private Path getOutputFilePath(User user, String fileName) {
        return Path.of("..", "script", String.valueOf(user.getUserId()), "output", fileName).normalize();
    }

    public String getScriptContentById(Long id) throws IOException {
        Script script = scriptRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Script not found"));
        Path scriptPath = Path.of(script.getLocation());
        return Files.readString(scriptPath);
    }

}
