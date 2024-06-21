package esgi.codelink.service.script;

import esgi.codelink.dto.script.ScriptDTO;
import esgi.codelink.entity.CustomUserDetails;
import esgi.codelink.entity.Script;
import esgi.codelink.entity.Token;
import esgi.codelink.entity.User;
import esgi.codelink.enumeration.ProtectionLevel;
import esgi.codelink.repository.ScriptRepository;
import esgi.codelink.repository.TokenRepository;
import esgi.codelink.repository.UserRepository;
import esgi.codelink.service.AuthService;
import esgi.codelink.service.TokenService;
import esgi.codelink.service.UserService;
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

    @Autowired
    private AuthService authService;

    /**
     * Retrieves all scripts.
     *
     * @return a list of ScriptDTO objects representing all scripts.
     */
    public List<ScriptDTO> getAllScripts() {
        return scriptRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Retrieves a script by its ID.
     *
     * @param id the ID of the script to retrieve.
     * @return the ScriptDTO object corresponding to the specified ID, or null if not found.
     */
    public ScriptDTO getScriptById(Long id) {
        return scriptRepository.findById(id).map(this::convertToDTO).orElse(null);
    }

    /**
     * Creates a script location path.
     *
     * @param script the Script object to create the location path for.
     */
    private void makeScriptLocation(Script script) {
        String complement = "";
        switch (script.getLanguage()) {
            case "Python":
                complement = "/python";
                break;
            default:
                break;
        }
        complement += "/" + script.getUser().getUserId() + "/";
        script.setLocation(SCRIPTS_DIR.toString() + complement);
        System.out.println("location = " + script.getLocation());
        try {
            makeScriptRepoForUserIfNotExist(script);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user directory for saving scripts", e);
        }
        script.setLocation(script.getLocation() + script.getName() + ".py");
    }

    /**
     * Saves a script.
     *
     * @param customUserToken the token of the authenticated user.
     * @param scriptDTO       the ScriptDTO object containing script details.
     * @param scriptContent   the content of the script.
     * @return the saved ScriptDTO object.
     * @throws IOException if an error occurs while saving the script file.
     */
    public ScriptDTO saveScript(@AuthenticationPrincipal CustomUserDetails userDetails, ScriptDTO scriptDTO, String scriptContent) throws IOException {
        User authenticatedUser = userDetails.getUser();
        if (!(authenticatedUser.getUserId() == (scriptDTO.getUserId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to modify this script");
        }

        Script script = new Script(scriptDTO, authenticatedUser);
        makeScriptLocation(script);
        storeScriptFile(Path.of(script.getLocation()).normalize(), scriptContent);

        Script savedScript = scriptRepository.save(script);
        return convertToDTO(savedScript);
    }

    /**
     * Updates a script.
     *
     * @param id            the ID of the script to update.
     * @param scriptDTO     the ScriptDTO object containing updated script details.
     * @param scriptContent the updated content of the script.
     * @return the updated ScriptDTO object.
     * @throws IOException if an error occurs while updating the script file.
     */
    public ScriptDTO updateScript(@AuthenticationPrincipal CustomUserDetails userDetails, Long id, ScriptDTO scriptDTO, String scriptContent) throws IOException {
        User authenticatedUser = userDetails.getUser();
        if (!(authenticatedUser.getUserId() == (scriptDTO.getUserId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to modify this script");
        }
        Script existingScript = scriptRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Script not found"));

        User userOwner = userService.findById(scriptDTO.getUserId());
        if (userOwner == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown user trying to modify a script");
        }

        String oldLocation = existingScript.getLocation();
        existingScript.setName(scriptDTO.getName());
        existingScript.setProtectionLevel(ProtectionLevel.valueOf(scriptDTO.getProtectionLevel()));
        existingScript.setLanguage(scriptDTO.getLanguage());
        existingScript.setInputFiles(scriptDTO.getInputFiles());
        existingScript.setOutputFiles(scriptDTO.getOutputFiles());
        existingScript.setUser(userOwner);

        makeScriptLocation(existingScript);
        replaceScriptFile(Path.of(oldLocation), Path.of(existingScript.getLocation()), scriptContent);

        Script updatedScript = scriptRepository.save(existingScript);
        return convertToDTO(updatedScript);
    }

    /**
     * Deletes a script.
     *
     * @param id the ID of the script to delete.
     */
    public void deleteScript(@AuthenticationPrincipal CustomUserDetails userDetails, Long id) {
        User authenticatedUser = userDetails.getUser();
        Script script = scriptRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Script not found"));

        if (!(authenticatedUser.getUserId() == (script.getUser().getUserId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this script");
        }

        try {
            Files.deleteIfExists(Path.of(script.getLocation()));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete script file", e);
        }

        scriptRepository.delete(script);
    }

    /**
     * Creates the script repository for a user if it does not exist.
     *
     * @param script the Script object to create the repository for.
     * @throws IOException if an error occurs while creating the directory.
     */
    private void makeScriptRepoForUserIfNotExist(Script script) throws IOException {
        Path userDir = SCRIPTS_DIR.resolve(script.getLanguage().toLowerCase() + "/" + script.getUser().getUserId());
        System.out.println("Creating directory at: " + userDir);
        if (Files.notExists(userDir)) {
            Files.createDirectories(userDir);
        }
    }

    /**
     * Stores a script file.
     *
     * @param scriptPath    the path to store the script file.
     * @param scriptContent the content of the script.
     * @throws IOException if an error occurs while storing the file.
     */
    private void storeScriptFile(Path scriptPath, String scriptContent) throws IOException {
        System.out.println("Storing script at: " + scriptPath.toAbsolutePath().normalize());
        if (Files.exists(scriptPath)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A file with this name already exists. Please choose a different name.");
        }
        Files.createFile(scriptPath);
        Files.write(scriptPath, scriptContent.getBytes());
    }

    /**
     * Replaces a script file.
     *
     * @param oldLocation          the old file location.
     * @param scriptPath           the new file location.
     * @param changedScriptContent the new content of the script.
     * @throws IOException if an error occurs while replacing the file.
     */
    private void replaceScriptFile(Path oldLocation, Path scriptPath, String changedScriptContent) throws IOException {
        System.out.println("Storing script at: " + scriptPath.toAbsolutePath());
        if (Files.notExists(oldLocation)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The file you are trying to modify does not exist.");
        }
        if (Files.deleteIfExists(oldLocation)) {
            Files.createFile(scriptPath);
            Files.write(scriptPath, changedScriptContent.getBytes());
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete the old script file.");
        }
    }

    /**
     * Converts a Script entity to a ScriptDTO.
     *
     * @param script the Script entity to convert.
     * @return the ScriptDTO object.
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
     * Converts a ScriptDTO to a Script entity.
     *
     * @param scriptDTO the ScriptDTO to convert.
     * @return the Script entity.
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

    /**
     * Retrieves the authenticated user from the token.
     *
     * @param token the token of the authenticated user.
     * @return the authenticated User entity.
     */
    private User getAuthenticatedUser(String token) {
        //String tokenWithoutBearer = token.substring(7);
        return authService.getAuthenticatedUser(token);
    }
}
