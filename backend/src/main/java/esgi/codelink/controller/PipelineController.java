package esgi.codelink.controller;

import esgi.codelink.dto.pipeline.PipelineJobResponse;
import esgi.codelink.dto.pipeline.PipelineResponse;
import esgi.codelink.dto.pipeline.StartPipelineRequest;
import esgi.codelink.dto.pipeline.StartPipelineResponse;
import esgi.codelink.entity.CustomUserDetails;
import esgi.codelink.service.pipeline.PipelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/pipelines")
public class PipelineController {

    private final PipelineService pipelineService;

    @Autowired
    public PipelineController(PipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    @PostMapping
    public ResponseEntity<StartPipelineResponse> startPipeline(@AuthenticationPrincipal CustomUserDetails customUserDetails, @ModelAttribute StartPipelineRequest pipelineRequest) {
        var user = customUserDetails.getUser();
        var pipelineResponse = pipelineService.startPipeline(pipelineRequest, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(pipelineResponse);
    }


    @GetMapping("/{id}")
    public ResponseEntity<String> getPipelineById(@PathVariable Long id) {
        var pipeline = pipelineService.findPipelineById(id);
        System.out.println(pipeline);
        return ResponseEntity.ok(pipeline.toString());
    }

    @GetMapping
    public ResponseEntity<List<PipelineResponse>> getPipelinesByUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        var user = customUserDetails.getUser();
        var pipelines = pipelineService.findPipelinesByUser(user);
        System.out.println(pipelines);
        return ResponseEntity.ok(pipelines);
    }


    @GetMapping("/{id}/jobs")
    public ResponseEntity<List<PipelineJobResponse>> getPipelineJobs(@PathVariable Long id) {
        var jobs = pipelineService.findJobs(id);
        System.out.println(jobs);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/output/{jobId}")
    public ResponseEntity<Resource> getJobOutput(@PathVariable Long jobId) {
        try {
            var filePath = pipelineService.findJobOutput(jobId);
            System.out.println("file path : "+filePath);

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(filePath));

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
