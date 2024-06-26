package esgi.codelink.service.scriptAndFile.file;

import esgi.codelink.entity.User;
import esgi.codelink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {

    private static final Path SCRIPTS_DIR = Path.of("../script").normalize();

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    public FileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String saveFile(MultipartFile file, Long userId, String subFolder) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new RuntimeException("User not found");
            }
            User user = userOpt.get();
            String userFolderPath = String.valueOf(SCRIPTS_DIR);

            Path folderPath = Paths.get(userFolderPath, subFolder);
            Files.createDirectories(folderPath);

            Path filePath = folderPath.resolve(file.getOriginalFilename());
            Files.write(filePath, file.getBytes());

            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    public List<String> saveFiles(MultipartFile[] files, Long userId, String subFolder) {
        List<String> filePaths = new ArrayList<>();
        for (MultipartFile file : files) {
            filePaths.add(saveFile(file, userId, subFolder));
        }
        return filePaths;
    }

    public Resource loadFileAsResource(Long userId, String subFolder, String fileName) {
        try {
            String userFolderPath = "C:/jujutravail/coursESGIpresentiel/2023-2024/semestre2/projetAnnuel/origine/projet-annuel/script/python/" + userId;
            Path filePath = Paths.get(userFolderPath, subFolder, fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found " + fileName, e);
        }
    }
}
