package esgi.codelink.service.scriptAndFile.file;

import esgi.codelink.dto.script.FileDTO;
import esgi.codelink.entity.User;
import esgi.codelink.entity.script.File;
import esgi.codelink.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {

    private static final Path FILES_DIR = Path.of("../script").normalize();

    @Autowired
    private FileRepository fileRepository;

    public List<FileDTO> getFilesByUser(User user) {
        return fileRepository.findByUserUserId(user.getUserId())
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<FileDTO> getGeneratedOrNotFilesByUser(User user, boolean isGenerated) {
        return fileRepository.findByUserUserIdAndIsGenerated(user.getUserId(), isGenerated)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<FileDTO> saveFiles(List<MultipartFile> files, boolean isGenerated, User user) throws IOException {
        List<File> savedFiles = files.stream()
                .map(file -> {
                    try {
                        return saveFile(file, isGenerated, user);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        return savedFiles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    public File saveFile(MultipartFile multipartFile, boolean isGenerated, User user) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        Path filePath = FILES_DIR.resolve(user.getUserId() + "/input").resolve(fileName).normalize();
        System.out.println("loccation file = " + filePath);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, multipartFile.getBytes());

        File file = new File(fileName, filePath.toString(), isGenerated, user);
        return fileRepository.save(file);
    }

    public File saveFile(File file,String content, boolean isGenerated, User user) throws IOException {
        String outputOrInput;
        if(isGenerated){
            outputOrInput = "/output";
        }
        else outputOrInput = "/input";
        Path filePath = FILES_DIR.resolve(user.getUserId() + outputOrInput).resolve(file.getName()).normalize();
        System.out.println("location file = " + filePath);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, content.getBytes());

        File ResultFile = new File(file.getName(), filePath.toString(), isGenerated, user);
        return fileRepository.save(ResultFile);
    }

    public FileDTO replaceFile(Long id, MultipartFile file, User user) throws IOException {
        File existingFile = fileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
        Path filePath = Path.of(existingFile.getLocation()).normalize();
        Files.write(filePath, file.getBytes());

        return convertToDTO(existingFile);
    }

    public void deleteFile(Long id, User user) throws IOException {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
        Path filePath = Path.of(file.getLocation()).normalize();
        Files.deleteIfExists(filePath);

        fileRepository.delete(file);
    }

    private FileDTO convertToDTO(File file) {
        FileDTO dto = new FileDTO(file.getName());
        dto.setId(file.getId());
        dto.setLocation(file.getLocation());
        dto.setGenerated(file.isGenerated());
        dto.setUserId(file.getUser().getUserId());
        return dto;
    }

    public File getFileById(Long id) {
        return fileRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
    }

    public String getFileContentById(Long id) throws IOException {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
        Path filePath = Path.of(file.getLocation());
        return Files.readString(filePath);
    }
}
