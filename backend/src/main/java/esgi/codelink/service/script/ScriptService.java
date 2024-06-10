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

    /**
     * Récupère tous les scripts sous forme de liste de ScriptDTO.
     *
     * @return une liste de ScriptDTO représentant tous les scripts stockés.
     */
    public List<ScriptDTO> getAllScripts() {
        return scriptRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Récupère un script par son identifiant.
     *
     * @param id l'identifiant du script à récupérer.
     * @return le ScriptDTO correspondant à l'identifiant fourni, ou null si aucun script n'est trouvé.
     */
    public ScriptDTO getScriptById(Long id) {
        return scriptRepository.findById(id).map(this::convertToDTO).orElse(null);
    }


    /**
     * Crée et configure l'emplacement de stockage d'un script en fonction de son langage et de l'utilisateur propriétaire.
     *
     * @param script l'objet Script pour lequel définir l'emplacement de stockage.
     */
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


    /**
     * Sauvegarde un script en base de données et en local.
     *
     * @param scriptDTO     le DTO du script à sauvegarder.
     * @param scriptContent le contenu du script à sauvegarder.
     * @return le ScriptDTO du script sauvegardé.
     * @throws IOException en cas d'erreur lors de la sauvegarde du fichier local.
     */
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

    /**
     * Crée le répertoire pour les scripts de l'utilisateur s'il n'existe pas déjà.
     *
     * @param script l'objet Script pour lequel créer le répertoire utilisateur.
     * @throws IOException en cas d'erreur lors de la création du répertoire.
     */
    private void makeScriptRepoForUserIfNotexist(Script script) throws IOException{
        Path userDir = SCRIPTS_DIR.resolve(script.getLanguage().toLowerCase() + "/" + script.getUser().getUserId());
        System.out.println("creation du repertoire a l'emplacement : " + userDir);
        if (Files.notExists(userDir)) {
            Files.createDirectories(userDir);
        }
    }


    /**
     * Sauvegarde localement le fichier de script.
     *
     * @param scriptPath    le chemin du fichier à sauvegarder.
     * @param scriptContent le contenu du script à sauvegarder.
     * @throws IOException en cas d'erreur lors de la sauvegarde du fichier.
     */
    // To locally save the script file
    private void storeScriptFile(Path scriptPath, String scriptContent) throws IOException {
        System.out.println("Storing script at: " + scriptPath.toAbsolutePath());
        if(Files.exists(scriptPath)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "vous devez effectuer une opération de modification, le fichier existe déjà.");
        }
        Files.createFile(scriptPath);
        Files.write(scriptPath, scriptContent.getBytes());
    }

    /**
     * Remplace le contenu d'un fichier de script existant.
     *
     * @param scriptPath           le chemin du fichier à remplacer.
     * @param changedScriptContent le nouveau contenu du script.
     * @throws IOException en cas d'erreur lors du remplacement du fichier.
     */
    private void replaceScriptFile(Path scriptPath, String changedScriptContent) throws IOException {
        System.out.println("Storing script at: " + scriptPath.toAbsolutePath());
        if(Files.notExists(scriptPath)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "vous devez effectuer une opération de création, le fichier n'existe pas.");
        }
        Files.write(scriptPath, changedScriptContent.getBytes());
    }

    /**
     * Convertit un objet Script en ScriptDTO.
     *
     * @param script l'objet Script à convertir.
     * @return le ScriptDTO correspondant.
     */
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

    /**
     * Convertit un objet ScriptDTO en entité Script.
     *
     * @param scriptDTO le ScriptDTO à convertir.
     * @return l'entité Script correspondante.
     */
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
