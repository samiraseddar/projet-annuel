package esgi.codelink.controller;

import esgi.codelink.dto.file.FileDTO;
import esgi.codelink.entity.CustomUserDetails;
import esgi.codelink.service.scriptAndFile.file.FileService;
import esgi.codelink.entity.script.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping
    public ResponseEntity<List<FileDTO>> getAllFiles(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<FileDTO> files = fileService.getAllFilesByUser(userDetails.getUser()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(files);
    }

    @GetMapping("/inputs")
    public ResponseEntity<List<FileDTO>> getInputFiles(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<FileDTO> inputFiles = fileService.getInputFilesByUser(userDetails.getUser()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(inputFiles);
    }

    @GetMapping("/outputs")
    public ResponseEntity<List<FileDTO>> getOutputFiles(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<FileDTO> outputFiles = fileService.getOutputFilesByUser(userDetails.getUser()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(outputFiles);
    }

    @PostMapping
    public ResponseEntity<FileDTO> uploadFile(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        File file = fileService.saveFile(multipartFile, false, userDetails.getUser());
        return ResponseEntity.ok(convertToDTO(file));
    }

    @GetMapping("/content/{id}")
    public ResponseEntity<Resource> getFileContent(@PathVariable Long id) throws IOException {
        Resource file = fileService.getFileContent(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FileDTO> updateFile(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        File updatedFile = fileService.updateFile(id, multipartFile, userDetails.getUser());
        return ResponseEntity.ok(convertToDTO(updatedFile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) throws IOException {
        fileService.deleteFile(id, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }

    private FileDTO convertToDTO(File file) {
        FileDTO fileDTO = new FileDTO(file.getName());
        fileDTO.setId(file.getId());
        fileDTO.setLocation(file.getLocation());
        fileDTO.setGenerated(file.isGenerated());
        fileDTO.setUserId(file.getUser().getUserId());
        return fileDTO;
    }
}
