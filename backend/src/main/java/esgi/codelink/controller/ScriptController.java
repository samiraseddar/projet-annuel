package esgi.codelink.controller;

import esgi.codelink.dto.script.ExecutionRequest;
import esgi.codelink.dto.script.ScriptDTO;
import esgi.codelink.dto.script.ScriptRequest;
import esgi.codelink.entity.CustomUserDetails;
import esgi.codelink.service.scriptAndFile.script.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/scripts")
public class ScriptController {

    @Autowired
    private ScriptService scriptService;

    @GetMapping
    public ResponseEntity<List<ScriptDTO>> getAllScripts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ScriptDTO> scripts = scriptService.getAllScriptsByUser(userDetails.getUser());
        return ResponseEntity.ok(scripts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScriptDTO> getScriptById(@PathVariable Long id) {
        ScriptDTO script = scriptService.getScriptById(id);
        return script != null ? ResponseEntity.ok(script) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ScriptDTO> createScript(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ScriptRequest scriptRequest) throws IOException {
        ScriptDTO createdScript = scriptService.saveScript(scriptRequest.getScriptDTO(), scriptRequest.getScriptContent(), userDetails.getUser());
        return ResponseEntity.ok(createdScript);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ScriptDTO> updateScript(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id, @RequestBody ScriptRequest scriptRequest) throws IOException {
        scriptRequest.getScriptDTO().setId(id);
        ScriptDTO updatedScript = scriptService.updateScript(scriptRequest.getScriptDTO(), scriptRequest.getScriptContent(), userDetails.getUser());
        return ResponseEntity.ok(updatedScript);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScript(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) throws IOException {
        scriptService.deleteScript(id, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/execute/{id}")
    public ResponseEntity<String> executeScript(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @RequestBody ExecutionRequest executionRequest
    ) throws IOException {
        String result = scriptService.executeScript(id, userDetails.getUser(), executionRequest.getFileIds(), executionRequest.getScriptIds());
        return ResponseEntity.ok(result);
    }

}
