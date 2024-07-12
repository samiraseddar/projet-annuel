package esgi.codelink.service.scriptAndFile.file;

import esgi.codelink.dto.file.FileDTO;
import esgi.codelink.entity.CustomUserDetails;
import esgi.codelink.entity.script.File;
import esgi.codelink.repository.FileRepository;
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
public class FileService {

    private static final Path BASE_DIR = Path.of("../script").normalize();

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserRepository userRepository;

    public List<FileDTO> getAllFiles(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return fileRepository.findByUserUserId(userDetails.getUser().getUserId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FileDTO> getFilesByType(@AuthenticationPrincipal CustomUserDetails userDetails, boolean isGenerated) {
        return fileRepository.findByUserUserIdAndIsGenerated(userDetails.getUser().getUserId(), isGenerated).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FileDTO saveFile(@AuthenticationPrincipal CustomUserDetails userDetails, FileDTO fileDTO, String fileContent) throws IOException {
        esgi.codelink.entity.User user = userRepository.findById(userDetails.getUser().getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        File file = new File(fileDTO.getName(), getFileLocation(userDetails, false, fileDTO.getName()), fileDTO.isGenerated(), user);
        storeFile(file, fileContent);

        File savedFile = fileRepository.save(file);
        return convertToDTO(savedFile);
    }

    public FileDTO updateFile(@AuthenticationPrincipal CustomUserDetails userDetails, Long id, FileDTO fileDTO, String fileContent) throws IOException {
        File existingFile = fileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));

        esgi.codelink.entity.User user = userRepository.findById(userDetails.getUser().getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (existingFile.getUser().getUserId() != user.getUserId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this file.");
        }

        String oldLocation = existingFile.getLocation();
        existingFile.setName(fileDTO.getName());
        existingFile.setLocation(getFileLocation(userDetails, fileDTO.isGenerated(), fileDTO.getName()));
        existingFile.setGenerated(false); //uploaded file can not be generated
        //existingFile.setUser(user);   //not wanted

        replaceFile(Path.of(oldLocation), Path.of(existingFile.getLocation()), fileContent);

        File updatedFile = fileRepository.save(existingFile);
        return convertToDTO(updatedFile);
    }

    public void deleteFile(@AuthenticationPrincipal CustomUserDetails userDetails, Long id) {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));

        if (file.getUser().getUserId() != (userDetails.getUser().getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this file.");
        }

        try {
            Files.deleteIfExists(Path.of(file.getLocation()));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete file", e);
        }

        fileRepository.delete(file);
    }

    private String getFileLocation(CustomUserDetails userDetails, boolean isGenerated, String fileName) {
        String folder = isGenerated ? "output" : "input";
        return BASE_DIR.resolve(String.valueOf(userDetails.getUser().getUserId()))
                .resolve(folder)
                .resolve(fileName)
                .toString();
    }

    private void storeFile(File file, String fileContent) throws IOException {
        Path filePath = Path.of(file.getLocation()).normalize();
        if (Files.exists(filePath)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File already exists.");
        }

        // Créer les répertoires nécessaires
        Files.createDirectories(filePath.getParent());

        Files.createFile(filePath);
        Files.write(filePath, fileContent.getBytes());

        if (Files.notExists(filePath)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create the file at " + filePath.toAbsolutePath());
        }
    }

    private void replaceFile(Path oldLocation, Path newLocation, String fileContent) throws IOException {
        if (Files.notExists(oldLocation)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File does not exist.");
        }
        if (Files.deleteIfExists(oldLocation)) {
            Files.createFile(newLocation);
            Files.write(newLocation, fileContent.getBytes());
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete the old file.");
        }
    }

    private FileDTO convertToDTO(File file) {
        FileDTO dto = new FileDTO();
        dto.setId(file.getId());
        dto.setName(file.getName());
        dto.setLocation(file.getLocation());
        dto.setGenerated(file.isGenerated());
        dto.setUserId(file.getUser().getUserId());
        return dto;
    }
}