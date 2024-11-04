package esgi.codelink.dto.pipeline;

import esgi.codelink.entity.pipeline.PipelineJob;

public class PipelineJobResponse {
    private Long id;

    private String script_name;

    private String inputFile;

    private String outputFile;

    private PipelineJob.JobStatus status;

    public PipelineJobResponse() {
    }

    public PipelineJobResponse(Long id, String script_name, String inputFile, String outputFile, PipelineJob.JobStatus status) {
        this.id = id;
        this.script_name = script_name;
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScript_name() {
        return script_name;
    }

    public void setScript_name(String script_name) {
        this.script_name = script_name;
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public PipelineJob.JobStatus getStatus() {
        return status;
    }

    public void setStatus(PipelineJob.JobStatus status) {
        this.status = status;
    }
}
