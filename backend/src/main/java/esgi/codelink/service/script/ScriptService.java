package esgi.codelink.service.script;

import esgi.codelink.dto.script.ScriptDTO;
import esgi.codelink.entity.CustomUserDetails;
import esgi.codelink.entity.Script;
import esgi.codelink.entity.User;
import esgi.codelink.enumeration.ProtectionLevel;
import esgi.codelink.repository.ScriptRepository;
import esgi.codelink.repository.UserRepository;
import esgi.codelink.service.UserService;
import esgi.codelink.service.script.differentScriptExecutor.PythonScriptExecutor;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScriptService {

    private static final Path SCRIPTS_DIR = Path.of("../script").normalize();

    @Autowired
    private ScriptRepository scriptRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private final ScriptExecutor scriptExecutor = new PythonScriptExecutor();

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
    private void makeScriptLocation(Script script) {
        String complement = "";
        switch (script.getLanguage()) {
            case "Python":
                complement = "\\python";
                break;
            default:
                break;
        }
        complement += "\\" + script.getUser().getUserId() + "\\";
        script.setLocation(SCRIPTS_DIR.toString() + complement);
        System.out.println("location = " + script.getLocation());
        try {
            makeScriptRepoForUserIfNotexist(script);
        } catch (IOException e) {
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
    public ScriptDTO saveScript(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid ScriptDTO scriptDTO, String scriptContent) throws IOException, ResponseStatusException {
        //récupération de l'utilisateur
        User userOwner = userDetails.getUser();

        userRepository.findById(userOwner.getUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "the user does not exist"));

        // Convertir le DTO en entité
        Script script = new Script(scriptDTO, userOwner);
        makeScriptLocation(script);

        // Sauvegarder le fichier localement
        Path scriptPath = Path.of(script.getLocation()).normalize();
        storeScriptFile(scriptPath, scriptContent);

        // Vérifier si le fichier a été créé
        if (Files.notExists(scriptPath)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create the script file at " + scriptPath.toAbsolutePath());
        }

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
     * Met à jour un script existant.
     *
     * @param id            l'identifiant du script à mettre à jour.
     * @param scriptDTO     le DTO contenant les nouvelles informations du script.
     * @param scriptContent le nouveau contenu du script.
     * @return le ScriptDTO du script mis à jour.
     * @throws IOException en cas d'erreur lors de la mise à jour du fichier local.
     */
    public ScriptDTO updateScript(@AuthenticationPrincipal CustomUserDetails userDetails,Long id, ScriptDTO scriptDTO, String scriptContent) throws IOException {
        // Vérifier si le script existe
        Script existingScript = scriptRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Script has not been saved in the database"));


        User userOwner = userDetails.getUser();
        // Récupération de l'utilisateur
        //userOwner = userService.findById(userOwner.getUserId());    //pour vérifier que le user existe bien
        if (userOwner == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown user trying to modify a script");
        }
        else if(userOwner.getUserId() != existingScript.getUser().getUserId()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this script, you cannot update it.");
        }

        // Mise à jour des informations du script
        String oldLocation = existingScript.getLocation();
        existingScript.setName(scriptDTO.getName());
        existingScript.setProtectionLevel(ProtectionLevel.valueOf(scriptDTO.getProtectionLevel()));
        existingScript.setLanguage(scriptDTO.getLanguage());
        existingScript.setInputFiles(scriptDTO.getInputFiles());
        existingScript.setOutputFiles(scriptDTO.getOutputFiles());
        existingScript.setUser(userOwner);

        makeScriptLocation(existingScript);
        replaceScriptFile(Path.of(oldLocation), Path.of(existingScript.getLocation()), scriptContent);

        // Sauvegarder les modifications en base de données
        Script updatedScript = scriptRepository.save(existingScript);

        // Convertir l'entité sauvegardée en DTO
        return convertToDTO(updatedScript);
    }

    /**
     * Supprime un script existant.
     *
     * @param id l'identifiant du script à supprimer.
     */
    public void deleteScript(@AuthenticationPrincipal CustomUserDetails userDetails, Long id) {
        // Vérifier si le script existe
        User userOwner = userDetails.getUser();
        Script script = scriptRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Script not found"));

        if(script.getUser().getUserId() != userOwner.getUserId()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this script, you cannot delete it.");
        }

        // Supprimer le fichier local
        try {
            Files.deleteIfExists(Path.of(script.getLocation()));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete script file", e);
        }

        // Supprimer l'entité en base de données
        scriptRepository.delete(script);
    }

    /**
     * Crée le répertoire pour les scripts de l'utilisateur s'il n'existe pas déjà.
     *
     * @param script l'objet Script pour lequel créer le répertoire utilisateur.
     * @throws IOException en cas d'erreur lors de la création du répertoire.
     */
    private void makeScriptRepoForUserIfNotexist(Script script) throws IOException {
        Path userDir = SCRIPTS_DIR.resolve(script.getLanguage().toLowerCase() + "/" + script.getUser().getUserId());
        System.out.println("Creating directory at: " + userDir.toAbsolutePath().normalize());
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
    private void storeScriptFile(Path scriptPath, String scriptContent) throws IOException {
        System.out.println("Storing script at: " + scriptPath.toAbsolutePath().normalize());
        if (Files.exists(scriptPath)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You need to perform a modification operation, the file already exists.");
        }
        Files.createFile(scriptPath);
        Files.write(scriptPath, scriptContent.getBytes());

        // Vérifier si le fichier a été créé
        if (Files.notExists(scriptPath)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create the script file at " + scriptPath.toAbsolutePath());
        }
    }

    /**
     * Remplace le contenu d'un fichier de script existant.
     *
     * @param oldLocation          le chemin de l'ancien fichier.
     * @param scriptPath           le chemin du fichier à remplacer.
     * @param changedScriptContent le nouveau contenu du script.
     * @throws IOException en cas d'erreur lors du remplacement du fichier.
     */
    private void replaceScriptFile(Path oldLocation, Path scriptPath, String changedScriptContent) throws IOException {
        System.out.println("Replacing script at: " + scriptPath.toAbsolutePath());
        if (Files.notExists(oldLocation)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You need to perform a creation operation, the file does not exist.");
        }
        if (Files.deleteIfExists(oldLocation)) {
            Files.createFile(scriptPath);
            Files.write(scriptPath, changedScriptContent.getBytes());
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete the script.");
        }
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

    public String executeScript(@AuthenticationPrincipal CustomUserDetails userDetails, Long id) {

        User UserExecutor = userDetails.getUser();
        Script script = scriptRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no script of this id"));

        if(script.getProtectionLevel() == ProtectionLevel.PUBLIC || UserExecutor.getUserId() == script.getUser().getUserId() ){
            return scriptExecutor.executeScript(script.getLocation());
        }
        else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you are not the owner of this script, you can't execute it");
        }

    }

}
