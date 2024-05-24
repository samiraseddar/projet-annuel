package esgi.codelink.controller;

import esgi.codelink.dto.script.ScriptDTO;
import esgi.codelink.dto.script.ScriptRequest;
import esgi.codelink.service.script.ScriptExecutor;
import esgi.codelink.service.script.ScriptService;
import esgi.codelink.service.script.differentScriptExecutor.pythonScriptExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/scripts")
public class ScriptController {

    @Autowired
    private ScriptService scriptService;

    private ScriptExecutor scriptExecutor = new pythonScriptExecutor();

    @GetMapping
    public ResponseEntity<List<ScriptDTO>> getAllScripts() {
        return ResponseEntity.ok(scriptService.getAllScripts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScriptDTO> getScriptById(@PathVariable Long id) {
        ScriptDTO scriptDTO = scriptService.getScriptById(id);
        return scriptDTO != null ? ResponseEntity.ok(scriptDTO) : ResponseEntity.notFound().build();
    }

    @GetMapping("/execute")
    public ResponseEntity<String> getScriptById(@RequestBody String monScriptEnStr) {
        // Supprimer tous les espaces avant les autres caractères
        String scriptSansEspaces = monScriptEnStr.replaceAll("^\\s+", "");
        String scriptResult = scriptExecutor.executeRawScript(scriptSansEspaces);
        return ResponseEntity.ok(scriptResult);
    }

    @PostMapping
    public ResponseEntity<ScriptDTO> createScript(@RequestBody ScriptRequest scriptRequest) throws IOException {
        ScriptDTO createdScript = scriptService.saveScript(scriptRequest.getScriptDTO(), scriptRequest.getScriptContent());
        return ResponseEntity.ok(createdScript);
    }

}
