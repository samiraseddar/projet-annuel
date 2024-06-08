package esgi.codelink.service.script;

import esgi.codelink.dto.script.ScriptDTO;
import esgi.codelink.entity.Script;
import esgi.codelink.entity.User;
import esgi.codelink.enumeration.ProtectionLevel;
import esgi.codelink.repository.ScriptRepository;
import esgi.codelink.repository.UserRepository;
import esgi.codelink.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        // Définir le chemin relatif au dossier des scripts
        SCRIPTS_DIR = Path.of("backend/src/main/script");
    }

    public List<ScriptDTO> getAllScripts() {
        return scriptRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ScriptDTO getScriptById(Long id) {
        return scriptRepository.findById(id).map(this::convertToDTO).orElse(null);
    }

    private void makeScriptLocation(Script script){
        String complement = "";
        switch (script.getLanguage()){
            case "Python" :
                complement = "\\python";
                break;
            default:
                break;
        }
        complement += "\\" + script.getUser().getUserId() + "\\";
        script.setLocation(SCRIPTS_DIR.toString() + complement);
        System.out.println("location = " + script.getLocation());
        try{
            makeScriptRepoForUserIfNotexist(script);
        }
        catch (IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user directory for saving his scripts", e);
        }

        script.setLocation(script.getLocation() + script.getName() + ".py");
    }

    public ScriptDTO saveScript(ScriptDTO scriptDTO, String scriptContent) throws IOException {
        //récupération de l'utilisateur
        User userOwner = userService.findById(scriptDTO.getUserId());

        // Convertir le DTO en entité
        Script script = new Script(scriptDTO, userOwner);
        makeScriptLocation(script);
        storeScriptFile(Path.of(script.getLocation()), scriptContent);

        // Sauvegarder l'entité
        Script savedScript = scriptRepository.save(script);





        // Convertir l'entité sauvegardée en DTO
        ScriptDTO savedScriptDTO = new ScriptDTO();
        savedScriptDTO.setId(savedScript.getScript_id());
        savedScriptDTO.setName(savedScript.getName());
        savedScriptDTO.setLocation(savedScript.getLocation());
        savedScriptDTO.setProtectionLevel(savedScript.getProtectionLevel().name());
        savedScriptDTO.setLanguage(savedScript.getLanguage());
        savedScriptDTO.setInputFiles(savedScript.getInputFiles());
        savedScriptDTO.setOutputFiles(savedScript.getOutputFiles());
        savedScriptDTO.setUserId(savedScript.getUser().getUserId());

        return savedScriptDTO;
    }

    private void makeScriptRepoForUserIfNotexist(Script script) throws IOException{
        Path userDir = SCRIPTS_DIR.resolve(script.getLanguage().toLowerCase() + "/" + script.getUser().getUserId());
        System.out.println("creation du repertoire a l'emplacement : " + userDir);
        if (Files.notExists(userDir)) {
            Files.createDirectories(userDir);
            System.out.println("creation du repertoire");
        }
        else{

        }
    }


    // To locally save the script file
    private void storeScriptFile(Path scriptPath, String scriptContent) throws IOException {
        System.out.println("Storing script at: " + scriptPath.toAbsolutePath());
        if(Files.exists(scriptPath)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "vous devez effectuer une opération de modification, le fichier existe déjà.");
        }
        Files.createFile(scriptPath);
        Files.write(scriptPath, scriptContent.getBytes());
    }

//    private void storeScriptFile(String scriptName, String scriptContent) throws IOException {
//        Path scriptPath = SCRIPTS_DIR.resolve(scriptName + ".py");
//        Files.createDirectories(scriptPath.getParent());
//        Files.write(scriptPath, scriptContent.getBytes());
//    }

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
