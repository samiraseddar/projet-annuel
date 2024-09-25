package esgi.codelink.dto.script;

import esgi.codelink.enumeration.ProtectionLevel;

public class UpdateLibraryRequest {
    private String name;
    private ProtectionLevel protectionLevel;

    // Getters et setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProtectionLevel getProtectionLevel() {
        return protectionLevel;
    }

    public void setProtectionLevel(ProtectionLevel protectionLevel) {
        this.protectionLevel = protectionLevel;
    }
}
