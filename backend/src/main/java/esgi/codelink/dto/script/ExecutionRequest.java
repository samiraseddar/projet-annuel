package esgi.codelink.dto.script;

import java.util.List;

public class ExecutionRequest {
    private List<Long> fileIds;
    private List<Long> scriptIds;

    public List<Long> getFileIds() {
        return fileIds;
    }

    public void setFileIds(List<Long> fileIds) {
        this.fileIds = fileIds;
    }

    public List<Long> getScriptIds() {
        return scriptIds;
    }

    public void setScriptIds(List<Long> scriptIds) {
        this.scriptIds = scriptIds;
    }
}
