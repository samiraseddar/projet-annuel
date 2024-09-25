package esgi.codelink.dto.script;


import esgi.codelink.entity.ScriptLibrary;
import esgi.codelink.entity.script.Script;

import java.util.List;
import java.util.Set;

public class LibraryDTO {

    private Long libraryId;
    private String name;
    private String protectionLevel;
    private List<ScriptDTO> scripts;

    public LibraryDTO(Long libraryId, String name, String protectionLevel, List<ScriptDTO> scripts) {
        this.libraryId = libraryId;
        this.name = name;
        this.protectionLevel = protectionLevel;
        this.scripts = scripts;
    }

    public LibraryDTO(ScriptLibrary library) {
        this.libraryId = library.getLibraryId();
        this.name = library.getName();
        this.protectionLevel = library.getProtectionLevel().name();
        this.scripts = library.getScripts()
                .stream()
                .map(Script::toDTO)  // Convert each Script entity to ScriptDTO using the toDTO method
                .toList();
    }

    public Long getLibraryId() {
        return libraryId;
    }

    public String getName() {
        return name;
    }

    public String getProtectionLevel() {
        return protectionLevel;
    }

    public List<ScriptDTO> getScripts() {
        return scripts;
    }
}
