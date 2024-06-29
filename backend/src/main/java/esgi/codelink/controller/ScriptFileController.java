package esgi.codelink.controller;

import esgi.codelink.dto.script.ScriptFileDTO;
import esgi.codelink.entity.CustomUserDetails;
import esgi.codelink.service.scriptAndFile.file.ScriptFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/api/files")
public class ScriptFileController {

    @Autowired
    private ScriptFileService scriptFileService;

    @GetMapping
    public ResponseEntity<List<ScriptFileDTO>> getAllFiles(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ScriptFileDTO> files = scriptFileService.getFilesByUserId(userDetails.getUser().getUserId());
        return ResponseEntity.ok(files);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScriptFileDTO> getFileById(@PathVariable Long id) {
        ScriptFileDTO file = scriptFileService.getFileById(id);
        return file != null ? ResponseEntity.ok(file) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ScriptFileDTO> uploadFile(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("file") MultipartFile file, @RequestParam("isGenerated") boolean isGenerated) throws IOException {
        ScriptFileDTO fileDTO = new ScriptFileDTO();
        fileDTO.setName(file.getOriginalFilename());
        fileDTO.setGenerated(isGenerated);
        ScriptFileDTO createdFile = scriptFileService.saveFile(userDetails, fileDTO, file.getBytes());
        return ResponseEntity.ok(createdFile);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) throws IOException {
        scriptFileService.deleteFile(userDetails, id);
        return ResponseEntity.noContent().build();
    }
}
