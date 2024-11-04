package esgi.codelink.service;

import esgi.codelink.entity.pipeline.Pipeline;
import esgi.codelink.entity.pipeline.PipelineJob;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendJobStatusUpdate(Long pipelineId, Long jobId, Long script_id, String scriptName, PipelineJob.JobStatus status) {
        System.out.println("send job status message | Pipeline id : " + pipelineId + " | job id : " + jobId);
        JobStatusMessage message = new JobStatusMessage(pipelineId, jobId, scriptName, status.name(), script_id);
        messagingTemplate.convertAndSend("/topic/jobStatus/" + pipelineId, message);
    }

    public void sendPipelineStatusUpdate(Long pipelineId, Pipeline.PipelineStatus status) {
        PipelineStatusMessage message = new PipelineStatusMessage(pipelineId, status);
        messagingTemplate.convertAndSend("/topic/pipelineStatus/" + pipelineId, message);
    }

    public static class JobStatusMessage {
        private Long pipelineId;
        private Long jobId;
        private String scriptName;
        private String status;

        private long script_id;

        public JobStatusMessage(Long pipelineId, Long jobId, String scriptName, String status, Long script_id) {
            this.pipelineId = pipelineId;
            this.jobId = jobId;
            this.scriptName = scriptName;
            this.status = status;
            this.script_id = script_id;
        }

        public Long getPipelineId() {
            return pipelineId;
        }

        public Long getJobId() {
            return jobId;
        }

        public String getScriptName() {
            return scriptName;
        }

        public String getStatus() {
            return status;
        }

        public long getScript_id() {
            return script_id;
        }
    }

    public static class PipelineStatusMessage {
        private Long pipelineId;
        private Pipeline.PipelineStatus status;

        public PipelineStatusMessage(Long pipelineId, Pipeline.PipelineStatus status) {
            this.pipelineId = pipelineId;
            this.status = status;
        }

        public Long getPipelineId() {
            return pipelineId;
        }

        public Pipeline.PipelineStatus getStatus() {
            return status;
        }
    }
}
