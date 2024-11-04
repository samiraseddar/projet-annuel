package esgi.codelink.dto.pipeline;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class StartPipelineRequest {
    private MultipartFile initialInputFile;
    private List<Long> scriptIds;

    public MultipartFile getInitialInputFile() {
        return initialInputFile;
    }

    public void setInitialInputFile(MultipartFile initialInputFile) {
        this.initialInputFile = initialInputFile;
    }

    public List<Long> getScriptIds() {
        return scriptIds;
    }

    public void setScriptIds(List<Long> scriptIds) {
        this.scriptIds = scriptIds;
    }
}
