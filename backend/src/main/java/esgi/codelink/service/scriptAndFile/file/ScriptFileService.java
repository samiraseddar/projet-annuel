package esgi.codelink.service.scriptAndFile.file;

import esgi.codelink.dto.script.ScriptFileDTO;
import esgi.codelink.entity.CustomUserDetails;
import esgi.codelink.entity.User;
import esgi.codelink.entity.script.ScriptFile;
import esgi.codelink.repository.ScriptFileRepository;
import esgi.codelink.repository.UserRepository;
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
public class ScriptFileService {

    private static final Path BASE_DIR = Path.of("../script").normalize();

    @Autowired
    private ScriptFileRepository scriptFileRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ScriptFileDTO> getFilesByUserId(Long userId) {
        return scriptFileRepository.findByUserId(userId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ScriptFileDTO getFileById(Long id) {
        return scriptFileRepository.findById(id).map(this::convertToDTO).orElse(null);
    }

    public ScriptFileDTO saveFile(@AuthenticationPrincipal CustomUserDetails userDetails, ScriptFileDTO fileDTO, byte[] content) throws IOException {
        //récupération de l'utilisateur
        User user = userDetails.getUser();

        userRepository.findById(user.getUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "The user does not exist"));

        // Convertir le DTO en entité
        ScriptFile file = new ScriptFile(user);
        file.setName(fileDTO.getName());
        file.setGenerated(fileDTO.getGenerated());

        String subDir;
        if(file.isGenerated()){
            subDir = "ouput";
        }
        else subDir = "input";
        Path filePath = BASE_DIR.resolve(String.valueOf(file.getUser().getUserId())).resolve(subDir).resolve(file.getName());
        file.setPath(filePath.toString());

        storeFile(filePath, content);

        ScriptFile savedFile = scriptFileRepository.save(file);

        return convertToDTO(savedFile);
    }

    public void deleteFile(@AuthenticationPrincipal CustomUserDetails userDetails, Long id) throws IOException {
        var user = userDetails.getUser();
        ScriptFile file = scriptFileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));

        if (file.getUser().getUserId() != (user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this file, you cannot delete it.");
        }

        Files.deleteIfExists(Path.of(file.getPath()));

        scriptFileRepository.delete(file);
    }

    private void storeFile(Path path, byte[] content) throws IOException {
        Files.createDirectories(path.getParent());
        Files.write(path, content);
    }

    private ScriptFileDTO convertToDTO(ScriptFile file) {
        ScriptFileDTO dto = new ScriptFileDTO();
        dto.setId(file.getId());
        dto.setName(file.getName());
        dto.setPath(file.getPath());
        dto.setGenerated(file.isGenerated());
        dto.setUserId(file.getUser().getUserId());
        return dto;
    }
}