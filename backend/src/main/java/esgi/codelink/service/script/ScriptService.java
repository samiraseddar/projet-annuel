package esgi.codelink.service.script;

import esgi.codelink.dto.script.ScriptDTO;
import esgi.codelink.entity.Script;
import esgi.codelink.entity.User;
import esgi.codelink.enumeration.ProtectionLevel;
import esgi.codelink.repository.ScriptRepository;
import esgi.codelink.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScriptService {

    private Path SCRIPTS_DIR;

    @Autowired
    private ScriptRepository scriptRepository;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        // Définir le chemin relatif au dossier des scripts
        SCRIPTS_DIR = Paths.get("..", "..", "..", "..", "script").toAbsolutePath().normalize();
    }

    public List<ScriptDTO> getAllScripts() {
        return scriptRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ScriptDTO getScriptById(Long id) {
        return scriptRepository.findById(id).map(this::convertToDTO).orElse(null);
    }

    public ScriptDTO saveScript(ScriptDTO scriptDTO, String scriptContent) throws IOException {
        if ("Python".equalsIgnoreCase(scriptDTO.getLanguage())) {
            validatePythonScript(scriptContent);
        }

        storeScriptFile(scriptDTO.getName(), scriptContent);

        Script script = convertToEntity(scriptDTO);
        script.setLocation(SCRIPTS_DIR.resolve(scriptDTO.getName() + ".py").toString());
        Script savedScript = scriptRepository.save(script);
        return convertToDTO(savedScript);
    }

    private void validatePythonScript(String scriptContent) {
        // Add your validation logic here.
    }

    private void storeScriptFile(String scriptName, String scriptContent) throws IOException {
        Path scriptPath = SCRIPTS_DIR.resolve(scriptName + ".py");
        Files.createDirectories(scriptPath.getParent());
        Files.write(scriptPath, scriptContent.getBytes());
    }

    private ScriptDTO convertToDTO(Script script) {
        ScriptDTO dto = new ScriptDTO();
        dto.setScriptId(script.getScript_id());
        dto.setName(script.getName());
        dto.setLocation(script.getLocation());
        dto.setProtectionLevel(script.getProtectionLevel().toString());
        dto.setLanguage(script.getLanguage());
        dto.setInputFiles(script.getInputFiles());
        dto.setOutputFiles(script.getOutputFiles());
        dto.setUserId(script.getUser().getUserId());
        return dto;
    }

    private Script convertToEntity(ScriptDTO scriptDTO) {
        Script script = new Script();
        script.setName(scriptDTO.getName());
        script.setProtectionLevel(ProtectionLevel.valueOf(scriptDTO.getProtectionLevel()));
        script.setLanguage(scriptDTO.getLanguage());
        script.setInputFiles(scriptDTO.getInputFiles());
        script.setOutputFiles(scriptDTO.getOutputFiles());

        User user = userRepository.findById(scriptDTO.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        script.setUser(user);
        return script;
    }
}
