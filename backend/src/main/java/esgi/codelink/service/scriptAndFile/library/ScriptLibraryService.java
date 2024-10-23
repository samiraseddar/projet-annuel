package esgi.codelink.service.scriptAndFile.library;

import esgi.codelink.dto.script.LibraryDTO;
import esgi.codelink.dto.script.ScriptDTO;
import esgi.codelink.dto.script.UpdateLibraryRequest;
import esgi.codelink.entity.ScriptLibrary;
import esgi.codelink.entity.User;
import esgi.codelink.entity.script.Script;
import esgi.codelink.enumeration.ProtectionLevel;
import esgi.codelink.repository.ScriptLibraryRepository;
import esgi.codelink.repository.ScriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScriptLibraryService {

    @Autowired
    private ScriptLibraryRepository scriptLibraryRepository;

    @Autowired
    private ScriptRepository scriptRepository;

    public LibraryDTO createLibrary(String name, ProtectionLevel protectionLevel, User owner) {
        ScriptLibrary library = new ScriptLibrary(owner);
        library.setName(name);
        library.setProtectionLevel(protectionLevel);
        return new LibraryDTO(scriptLibraryRepository.save(library));
    }

    public List<LibraryDTO> getAllLibrariesByUser(User owner) {
        List<ScriptLibrary> libraries = scriptLibraryRepository.findAllByOwnerOrProtectionLevel(owner, ProtectionLevel.PUBLIC);
        return libraries.stream().map(this::convertToLibraryDTO).collect(Collectors.toList());
    }


    public List<LibraryDTO> getExclusiveLibrariesFromUser(User owner) {
        List<ScriptLibrary> libraries = scriptLibraryRepository.findAllByOwner(owner);
        return libraries.stream().map(this::convertToLibraryDTO).collect(Collectors.toList());
    }
    public LibraryDTO addScriptToLibrary(Long libraryId, Long[] scriptIds, User owner) {
        // Récupération de la bibliothèque
        ScriptLibrary library = scriptLibraryRepository.findById(libraryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Library not found"));

        // Ajout de chaque script
        for (Long scriptId : scriptIds) {
            Script script = scriptRepository.findById(scriptId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Script with id " + scriptId + " not found"));

            // Vérification que le script appartient bien à l'utilisateur
            if (!script.getUser().equals(owner)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only add your own scripts to the library");
            }

            // Ajout du script à la bibliothèque s'il n'est pas déjà présent
            if (!library.getScripts().contains(script)) {
                library.addScript(script);
            }
        }

        // Sauvegarde de la bibliothèque mise à jour
        ScriptLibrary updatedLibrary = scriptLibraryRepository.save(library);
        return convertToLibraryDTO(updatedLibrary);
    }

    public void removeScriptFromLibrary(Long libraryId, Long scriptId) {
        ScriptLibrary library = scriptLibraryRepository.findById(libraryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Library not found"));

        Script script = scriptRepository.findById(scriptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Script not found"));

        library.removeScript(script);
        scriptLibraryRepository.save(library);
    }

    public void deleteLibrary(Long libraryId) {
        scriptLibraryRepository.deleteById(libraryId);
    }

    public LibraryDTO updateLibraryProtection(Long libraryId, ProtectionLevel protectionLevel) {
        ScriptLibrary library = scriptLibraryRepository.findById(libraryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Library not found"));

        library.setProtectionLevel(protectionLevel);
        ScriptLibrary updatedLibrary = scriptLibraryRepository.save(library);
        return convertToLibraryDTO(updatedLibrary);
    }

    // Method to convert a ScriptLibrary to LibraryDTO
    private LibraryDTO convertToLibraryDTO(ScriptLibrary library) {
        List<ScriptDTO> scriptDTOs = library.getScripts().stream()
                .map(this::convertToScriptDTO)
                .collect(Collectors.toList());

        return new LibraryDTO(library.getLibraryId(), library.getName(), library.getProtectionLevel().name(), scriptDTOs);
    }

    // Method to convert a Script to ScriptDTO
    private ScriptDTO convertToScriptDTO(Script script) {
        ScriptDTO result = new ScriptDTO(script.getScriptId());
        result.setName(script.getName());
        result.setLocation(script.getLocation());
        result.setProtectionLevel(script.getProtectionLevel().name());
        result.setLanguage(script.getLanguage());
        result.setInputFileExtensions(script.getInputFileExtensions());
        result.setOutputFileNames(script.getOutputFileNames());

        return result;
    }

    public List<Long> removeScriptsFromLibrary(Long libraryId, Long[] scriptIds) {
        // Récupérer la bibliothèque
        ScriptLibrary library = scriptLibraryRepository.findById(libraryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Library not found"));

        List<Long> failedToRemove = new ArrayList<>();

        for (Long scriptId : scriptIds) {
            // Rechercher chaque script à retirer
            Script script = scriptRepository.findById(scriptId)
                    .orElse(null);

            if (script != null && library.getScripts().contains(script)) {
                // Si le script est trouvé et appartient à la bibliothèque, on le retire
                library.removeScript(script);
            } else {
                // Sinon, ajouter l'ID du script à la liste des échecs
                failedToRemove.add(scriptId);
            }
        }

        // Sauvegarder les changements dans la bibliothèque
        scriptLibraryRepository.save(library);

        // Retourner la liste des scripts qui n'ont pas pu être retirés
        return failedToRemove;
    }

    public LibraryDTO updateLibrary(Long libraryId, UpdateLibraryRequest updateRequest) {
        // Récupérer la bibliothèque
        ScriptLibrary library = scriptLibraryRepository.findById(libraryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Library not found"));

        // Mise à jour du nom, si un nom est fourni
        if (updateRequest.getName() != null && !updateRequest.getName().isEmpty()) {
            library.setName(updateRequest.getName());
        }

        // Mise à jour du niveau de protection, si fourni
        if (updateRequest.getProtectionLevel() != null) {
            library.setProtectionLevel(updateRequest.getProtectionLevel());
        }

        // Sauvegarde des modifications dans la bibliothèque
        ScriptLibrary updatedLibrary = scriptLibraryRepository.save(library);

        return convertToLibraryDTO(updatedLibrary);
    }
}
