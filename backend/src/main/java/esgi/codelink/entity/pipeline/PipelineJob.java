package esgi.codelink.entity.pipeline;

import esgi.codelink.entity.script.Script;
import jakarta.persistence.*;

@Entity
@Table(name = "pipeline_jobs")
public class PipelineJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pipeline_id", nullable = false)
    private Pipeline pipeline;

    @ManyToOne
    @JoinColumn(name = "script_id", nullable = false)
    private Script script;

    private String inputFile;

    private String outputFile;

    @Enumerated(EnumType.STRING)
    private JobStatus status = JobStatus.PENDING;

    public enum JobStatus {
        PENDING,
        RUNNING,
        COMPLETED,
        FAILED
    }

    public Long getId() {
        return id;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
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

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PipelineJob{" +
                "id=" + id +
                ", script=" + script +
                ", inputFile='" + inputFile + '\'' +
                ", outputFile='" + outputFile + '\'' +
                ", status=" + status +
                '}';
    }
}
