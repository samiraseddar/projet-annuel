package esgi.codelink.dto.script;

public class ScriptRequest {

    private ScriptDTO scriptDTO;
    private String scriptContent;

    // Getters and Setters

    public ScriptDTO getScriptDTO() {
        return scriptDTO;
    }

    public void setScriptDTO(ScriptDTO scriptDTO) {
        this.scriptDTO = scriptDTO;
    }

    public String getScriptContent() {
        return scriptContent;
    }

    public void setScriptContent(String scriptContent) {
        this.scriptContent = scriptContent;
    }
}
