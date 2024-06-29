package esgi.codelink.dto.script;

public class ScriptFileRequest {

    private ScriptFileDTO scriptFileDTO;
    private String scriptContent;

    // Getters and Setters
    public ScriptFileDTO getScriptDTO() {
        return scriptFileDTO;
    }

    public void setScriptDTO(ScriptFileDTO scriptDTO) {
        this.scriptFileDTO = scriptDTO;
    }

    public String getScriptContent() {
        return scriptContent;
    }

    public void setScriptContent(String scriptContent) {
        this.scriptContent = scriptContent;
    }
}
