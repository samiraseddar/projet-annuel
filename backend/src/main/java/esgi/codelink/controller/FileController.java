package esgi.codelink.controller;

import esgi.codelink.dto.script.FileDTO;
import esgi.codelink.entity.CustomUserDetails;
import esgi.codelink.service.scriptAndFile.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping("")
    public ResponseEntity<List<FileDTO>> getAllFilesByUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<FileDTO> files = fileService.getFilesByUser(userDetails.getUser());
        return ResponseEntity.ok(files);
    }

    @GetMapping("/generated")
    public ResponseEntity<List<FileDTO>> getGeneratedFilesByUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<FileDTO> files = fileService.getGeneratedOrNotFilesByUser(userDetails.getUser(), true);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/uploaded")
    public ResponseEntity<List<FileDTO>> getUploadedFilesByUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<FileDTO> files = fileService.getGeneratedOrNotFilesByUser(userDetails.getUser(), false);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<String> getFileContentById(@PathVariable Long id) throws IOException {
        String content = fileService.getFileContentById(id);
        return ResponseEntity.ok(content);
    }

    @PostMapping
    public ResponseEntity<List<FileDTO>> uploadFiles(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("files") List<MultipartFile> files) throws IOException {
        List<FileDTO> savedFiles = fileService.saveFiles(files, false, userDetails.getUser());
        return ResponseEntity.ok(savedFiles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FileDTO> replaceFile(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        FileDTO replacedFile = fileService.replaceFile(id, file, userDetails.getUser());
        return ResponseEntity.ok(replacedFile);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) throws IOException {
        fileService.deleteFile(id, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }



}
