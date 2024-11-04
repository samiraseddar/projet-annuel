package esgi.codelink.dto.pipeline;

import esgi.codelink.entity.pipeline.Pipeline;

public class PipelineResponse {

    private long id;

    private String name;

    private Pipeline.PipelineStatus status;

    public PipelineResponse() {
    }

    public PipelineResponse(long id, String name, Pipeline.PipelineStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Pipeline.PipelineStatus getStatus() {
        return status;
    }

    public void setStatus(Pipeline.PipelineStatus status) {
        this.status = status;
    }
}
