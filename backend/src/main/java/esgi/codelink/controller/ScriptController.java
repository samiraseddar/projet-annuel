package esgi.codelink.controller;

import esgi.codelink.dto.script.ScriptDTO;
import esgi.codelink.dto.script.ScriptRequest;
import esgi.codelink.entity.CustomUserDetails;
import esgi.codelink.service.script.ScriptExecutor;
import esgi.codelink.service.script.ScriptService;
import esgi.codelink.service.script.differentScriptExecutor.pythonScriptExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/scripts")
public class ScriptController {

    @Autowired
    private ScriptService scriptService;

    private final ScriptExecutor scriptExecutor = new pythonScriptExecutor();

    @GetMapping
    public ResponseEntity<List<ScriptDTO>> getAllScripts() {
        List<ScriptDTO> scripts = scriptService.getAllScripts();
        return ResponseEntity.ok(scripts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScriptDTO> getScriptById(@PathVariable Long id) {
        ScriptDTO script = scriptService.getScriptById(id);
        return script != null ? ResponseEntity.ok(script) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ScriptDTO> createScript(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ScriptRequest scriptRequest) throws IOException {
        ScriptDTO createdScript = scriptService.saveScript(userDetails, scriptRequest.getScriptDTO(), scriptRequest.getScriptContent());
        return ResponseEntity.ok(createdScript);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ScriptDTO> updateScript(@AuthenticationPrincipal CustomUserDetails userDetails,@PathVariable Long id, @RequestBody ScriptRequest scriptRequest) throws IOException {
        ScriptDTO updatedScript = scriptService.updateScript(userDetails, id, scriptRequest.getScriptDTO(), scriptRequest.getScriptContent());
        return ResponseEntity.ok(updatedScript);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScript(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) {
        scriptService.deleteScript(userDetails, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/execute")
    public ResponseEntity<String> getScriptById(@RequestBody ScriptRequest monScriptEnStr) {
        // Supprimer tous les espaces avant les autres caractères
        String scriptSansEspaces = monScriptEnStr.getScriptContent().replaceAll("^\\s+", "");
        String scriptResult = scriptExecutor.executeRawScript(scriptSansEspaces);
        return ResponseEntity.ok(scriptResult);
    }

    @GetMapping("/execute/{id}")
    public ResponseEntity<String> executeScript(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) {
        try {
            String output = scriptService.executeScript(userDetails, id);
            return ResponseEntity.ok(output);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
