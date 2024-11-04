package esgi.codelink.dto.script;

import java.util.List;
import java.util.Map;

public class PipelineRequest {
    private Long initialScriptId;
    private Map<Long, List<Long>> scriptToFileMap;

    // Getters and Setters
    public Long getInitialScriptId() {
        return initialScriptId;
    }

    public void setInitialScriptId(Long initialScriptId) {
        this.initialScriptId = initialScriptId;
    }

    public Map<Long, List<Long>> getScriptToFileMap() {
        return scriptToFileMap;
    }

    public void setScriptToFileMap(Map<Long, List<Long>> scriptToFileMap) {
        this.scriptToFileMap = scriptToFileMap;
    }
}