package esgi.codelink.controller;

import esgi.codelink.dto.file.FileDTO;
import esgi.codelink.dto.file.FileRequest;
import esgi.codelink.entity.CustomUserDetails;
import esgi.codelink.service.scriptAndFile.file.FileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping
    public ResponseEntity<List<FileDTO>> getAllFiles(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<FileDTO> files = fileService.getAllFiles(userDetails);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/input")
    public ResponseEntity<List<FileDTO>> getInputFiles(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<FileDTO> inputFiles = fileService.getFilesByType(userDetails, false);
        return ResponseEntity.ok(inputFiles);
    }

    @GetMapping("/output")
    public ResponseEntity<List<FileDTO>> getOutputFiles(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<FileDTO> outputFiles = fileService.getFilesByType(userDetails, true);
        return ResponseEntity.ok(outputFiles);
    }

    @PostMapping
    public ResponseEntity<FileDTO> createFile(@AuthenticationPrincipal CustomUserDetails userDetails,@Valid @RequestBody FileRequest fileRequest) throws IOException {
        FileDTO createdFile = fileService.saveFile(userDetails, fileRequest.getFileDTO(), fileRequest.getFileContent());
        return ResponseEntity.ok(createdFile);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FileDTO> updateFile(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id,@Valid @RequestBody FileRequest fileRequest) throws IOException {
        FileDTO updatedFile = fileService.updateFile(userDetails, id, fileRequest.getFileDTO(), fileRequest.getFileContent());
        return ResponseEntity.ok(updatedFile);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) {
        fileService.deleteFile(userDetails, id);
        return ResponseEntity.noContent().build();
    }
}
