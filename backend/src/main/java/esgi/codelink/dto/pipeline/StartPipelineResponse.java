package esgi.codelink.dto.pipeline;

import java.util.List;

public class StartPipelineResponse {

    private List<String> scripts_names;

    private long id;

    public StartPipelineResponse() {
    }

    public StartPipelineResponse(List<String> scripts_names, long id) {
        this.scripts_names = scripts_names;
        this.id = id;
    }

    public List<String> getScripts_names() {
        return scripts_names;
    }

    public void setScripts_names(List<String> scripts_names) {
        this.scripts_names = scripts_names;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
