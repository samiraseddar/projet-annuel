package esgi.codelink.controller;

import esgi.codelink.dto.script.LibraryDTO;
import esgi.codelink.dto.script.ScriptDTO;
import esgi.codelink.dto.script.UpdateLibraryRequest;
import esgi.codelink.entity.CustomUserDetails;
import esgi.codelink.enumeration.ProtectionLevel;
import esgi.codelink.service.scriptAndFile.library.ScriptLibraryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libraries")
public class ScriptLibraryController {

    @Autowired
    private ScriptLibraryService scriptLibraryService;

    @PostMapping
    public ResponseEntity<LibraryDTO> createLibrary(
            @RequestBody @Valid LibraryDTO libraryDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Si protectionLevel n'est pas fourni, on le définit par défaut à PUBLIC
        ProtectionLevel protectionLevel = libraryDTO.getProtectionLevel() != null
                ? ProtectionLevel.valueOf(libraryDTO.getProtectionLevel())
                : ProtectionLevel.PUBLIC;

        LibraryDTO library = scriptLibraryService.createLibrary(
                libraryDTO.getName(), protectionLevel, userDetails.getUser());

        return ResponseEntity.ok(library);
    }

    @GetMapping("/user")
    public ResponseEntity<List<LibraryDTO>> getLibrariesByUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<LibraryDTO> libraries = scriptLibraryService.getExclusiveLibrariesFromUser(userDetails.getUser());
        return ResponseEntity.ok(libraries);
    }

    @GetMapping("/free")
    public ResponseEntity<List<LibraryDTO>> getAllFreeLibrairies(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<LibraryDTO> libraries = scriptLibraryService.getAllLibrariesByUser(userDetails.getUser());
        return ResponseEntity.ok(libraries);
    }

    @PostMapping("/{libraryId}/scripts")
    public ResponseEntity<LibraryDTO> addScriptToLibrary(
            @PathVariable Long libraryId,
            @RequestBody Long[] scriptIds,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        LibraryDTO library = scriptLibraryService.addScriptToLibrary(libraryId, scriptIds, userDetails.getUser());
        return ResponseEntity.ok(library);
    }

    @DeleteMapping("/{libraryId}/scripts")
    public ResponseEntity<?> removeScriptsFromLibrary(
            @PathVariable Long libraryId,
            @RequestBody Long[] scriptIds) {

        List<Long> failedToRemove = scriptLibraryService.removeScriptsFromLibrary(libraryId, scriptIds);

        if (!failedToRemove.isEmpty()) {
            // Si un ou plusieurs scripts n'ont pas été retirés, renvoyer un message d'erreur
            String errorMessage = "Failed to remove the following script IDs: " + failedToRemove.toString();
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(errorMessage);
        }

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{libraryId}")
    public ResponseEntity<LibraryDTO> updateLibrary(
            @PathVariable Long libraryId,
            @RequestBody UpdateLibraryRequest updateRequest) {

        // Appel au service pour effectuer la mise à jour
        LibraryDTO updatedLibrary = scriptLibraryService.updateLibrary(libraryId, updateRequest);
        return ResponseEntity.ok(updatedLibrary);
    }

    @DeleteMapping("/{libraryId}")
    public ResponseEntity<Void> deleteLibrary(@PathVariable Long libraryId) {
        scriptLibraryService.deleteLibrary(libraryId);
        return ResponseEntity.noContent().build();
    }
}
