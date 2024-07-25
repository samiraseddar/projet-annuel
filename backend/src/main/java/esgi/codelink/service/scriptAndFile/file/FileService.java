package esgi.codelink.service.scriptAndFile.file;

import esgi.codelink.entity.User;
import esgi.codelink.entity.script.File;
import esgi.codelink.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileService {

    private static final Path FILES_DIR = Paths.get("../files");

    @Autowired
    private FileRepository fileRepository;

    public File saveFile(File file, String content, boolean isGenerated, User user) throws IOException {
        String outputOrInput = isGenerated ? "/output" : "/input";
        Path filePath = FILES_DIR.resolve(user.getUserId() + outputOrInput).resolve(file.getName()).normalize();
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, content.getBytes());

        File resultFile = new File(file.getName(), filePath.toString(), isGenerated, user);
        return fileRepository.save(resultFile);
    }

    public File saveFile(MultipartFile multipartFile, boolean isGenerated, User user) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        Path filePath = FILES_DIR.resolve(user.getUserId() + "/input").resolve(fileName).normalize();
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, multipartFile.getBytes());

        File file = new File(fileName, filePath.toString(), isGenerated, user);
        return fileRepository.save(file);
    }

    public File updateFile(Long id, MultipartFile multipartFile, User user) throws IOException {
        File existingFile = findById(id);
        deleteFile(existingFile);

        String fileName = multipartFile.getOriginalFilename();
        Path filePath = FILES_DIR.resolve(user.getUserId() + "/input").resolve(fileName).normalize();
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, multipartFile.getBytes());

        existingFile.setName(fileName);
        existingFile.setLocation(filePath.toString());
        return fileRepository.save(existingFile);
    }

    public File findById(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }

    public Resource getFileContent(Long id) throws IOException {
        File file = findById(id);
        Path filePath = Path.of(file.getLocation()).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found or not readable");
        }
        return resource;
    }


    public void deleteFile(Long id, User user) throws IOException {
        File file = findById(id);
        deleteFile(file);
        fileRepository.delete(file);
    }

    private void deleteFile(File file) throws IOException {
        Path filePath = Paths.get(file.getLocation()).normalize();
        Files.deleteIfExists(filePath);
    }

    public List<File> getAllFilesByUser(User user) {
        return fileRepository.findByUserUserId(user.getUserId());
    }

    public List<File> getInputFilesByUser(User user) {
        return fileRepository.findByUserUserIdAndIsGenerated(user.getUserId(), false);
    }

    public List<File> getOutputFilesByUser(User user) {
        return fileRepository.findByUserUserIdAndIsGenerated(user.getUserId(), true);
    }

    public Path getFilesDir() {
        return FILES_DIR;
    }

    public String readContent(File file) throws IOException {
        Path filePath = Path.of(file.getLocation());
        return Files.readString(filePath);
    }
}
