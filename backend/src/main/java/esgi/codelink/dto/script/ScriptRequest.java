package esgi.codelink.dto.script;

public class ScriptRequest {

    private ScriptDTO scriptDTO;
    private String scriptContent;
    public ScriptRequest(ScriptDTO scriptDTO, String scriptContent) {
        this.scriptDTO = scriptDTO;
        this.scriptContent = scriptContent;
    }
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

    @Override
    public String toString() {
        return "ScriptRequest{" +
                "scriptDTO=" + scriptDTO +
                '}';
    }
}
