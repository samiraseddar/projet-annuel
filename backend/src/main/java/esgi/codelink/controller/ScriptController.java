package esgi.codelink.controller;

import esgi.codelink.dto.CommentDTO;
import esgi.codelink.dto.script.ScriptDTO;
import esgi.codelink.dto.script.ScriptRequest;
import esgi.codelink.dto.script.PipelineRequest;
import esgi.codelink.entity.Comment;
import esgi.codelink.entity.CustomUserDetails;
import esgi.codelink.entity.User;
import esgi.codelink.entity.script.Script;
import esgi.codelink.service.CommentService;
import esgi.codelink.service.scriptAndFile.script.ScriptService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/scripts")
public class ScriptController {

    private final ScriptService scriptService;

    private final CommentService commentService;

    @Autowired
    public ScriptController(ScriptService scriptService, CommentService commentService) {
        this.scriptService = scriptService;
        this.commentService = commentService;
    }

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

    @GetMapping("/{id}/content")
    public ResponseEntity<String> getScriptContent(@PathVariable Long id) {
        var script = scriptService.getScriptContent(id);
        System.out.println("script content : " + script);
        return ResponseEntity.ok(script);
    }

    @PostMapping
    public ResponseEntity<ScriptDTO> createScript(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ScriptRequest scriptRequest) {
        System.out.println("CONTROLLER SAVE SCRIPT : " + scriptRequest);

        try {
            ScriptDTO createdScript = scriptService.saveScript(scriptRequest.getScriptDTO(), scriptRequest.getScriptContent(), userDetails.getUser());
            return ResponseEntity.ok(createdScript);
        } catch (IOException e) {
            log.error("[ERROR] An IOException occurred while creating the script: {}", e.getMessage(), e); // Log with exception details
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            log.error("[ERROR] An unexpected error occurred: {}", e.getMessage(), e); // Log with exception details
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
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

    @PostMapping("/execute-pipeline")
    public ResponseEntity<String> executePipeline(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PipelineRequest pipelineRequest
    ) throws IOException, InterruptedException {
        log.info("Received request to execute pipeline with initial script ID: {} by user: {}", pipelineRequest.getInitialScriptId(), userDetails.getUsername());

        try {
            String result = scriptService.executePipeline(
                    pipelineRequest.getInitialScriptId(),
                    userDetails.getUser(),
                    pipelineRequest.getScriptToFileMap()
            );

            log.info("Pipeline executed successfully for script ID: {}. Result: {}", pipelineRequest.getInitialScriptId(), result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error executing pipeline for script ID: {}", pipelineRequest.getInitialScriptId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error executing pipeline: " + e.getMessage());
        }
    }

    @PostMapping("/{scriptId}/comments/")
    public ResponseEntity<Comment> addComment(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CommentDTO commentDTO, @PathVariable Long scriptId) {
        System.out.println("add comment controller : " + commentDTO);
        ScriptDTO scriptDTO = scriptService.getScriptById(scriptId);
        System.out.println(scriptDTO);

        User user = userDetails.getUser();
        System.out.println("user : " + user);

        Script script = new Script(user, scriptDTO);

        Comment savedComment = commentService.addComment(commentDTO, script, user);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }


    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long commentId) {

        commentService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{scriptId}/comments/")
    public ResponseEntity<List<Comment>> getCommentsByScript(@PathVariable Long scriptId) {
        System.out.println("récupérer commentaires controller : " + scriptId);
        ScriptDTO script = scriptService.getScriptById(scriptId);

        List<Comment> comments = commentService.getCommentsByScript(script.getId());
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
}
