package esgi.codelink.service.pipeline;

import esgi.codelink.dto.pipeline.PipelineJobResponse;
import esgi.codelink.dto.pipeline.PipelineResponse;
import esgi.codelink.dto.pipeline.StartPipelineRequest;
import esgi.codelink.dto.pipeline.StartPipelineResponse;
import esgi.codelink.entity.User;
import esgi.codelink.entity.pipeline.Pipeline;
import esgi.codelink.entity.pipeline.PipelineJob;
import esgi.codelink.entity.script.Script;
import esgi.codelink.repository.PipelineJobRepository;
import esgi.codelink.repository.PipelineRepository;
import esgi.codelink.repository.ScriptRepository;
import esgi.codelink.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class PipelineService {

    private final PipelineRepository pipelineRepository;
    private final PipelineJobRepository pipelineJobRepository;
    private final ScriptRepository scriptRepository;
    private final WebSocketService webSocketService;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);;

    Path SCRIPTS_DIR = Paths.get(System.getProperty("user.dir")).getParent().resolve("scripts");

    @Autowired
    public PipelineService(PipelineJobRepository pipelineJobRepository, PipelineRepository pipelineRepository, ScriptRepository scriptRepository, WebSocketService webSocketService) {
        this.pipelineRepository = pipelineRepository;
        this.scriptRepository = scriptRepository;
        this.webSocketService = webSocketService;
        this.pipelineJobRepository = pipelineJobRepository;
    }

    public StartPipelineResponse startPipeline(StartPipelineRequest pipelineRequest, User user) {
        Pipeline newPipeline = new Pipeline();
        newPipeline.setName("New Pipeline"); // A CHANGER
        newPipeline.setStatus(Pipeline.PipelineStatus.RUNNING);
        newPipeline.setUser(user);
        var pipeline = pipelineRepository.save(newPipeline);

        List<Long> scriptIds = pipelineRequest.getScriptIds();
        List<Script> scripts = scriptRepository.findAllById(scriptIds);

        var scripts_names = scripts.stream().map(Script::getName).toList();
        var pipelineResponse = new StartPipelineResponse(scripts_names, pipeline.getId());

        for (Script script : scripts) {
            PipelineJob newJob = new PipelineJob();
            newJob.setScript(script);
            newJob.setStatus(PipelineJob.JobStatus.PENDING);
            newJob.setPipeline(pipeline);
            var job = pipelineJobRepository.save(newJob);

            pipeline.getJobs().add(job);
        }

        executor.execute(() -> {
            String initialInputFilePath;
            try {
                initialInputFilePath = saveInitialInputFile(pipelineRequest.getInitialInputFile(), user.getUserId());
            } catch (IOException e) {
                throw new RuntimeException("Failed to save initial input file", e);
            }
            String currentInputFile = initialInputFilePath;
            for (var job : pipeline.getJobs()) {
                job.setInputFile(currentInputFile);
                job.setStatus(PipelineJob.JobStatus.RUNNING);
                webSocketService.sendJobStatusUpdate(pipeline.getId(), job.getId(), job.getScript().getScript_id(), job.getScript().getName(), job.getStatus());

                var script = job.getScript();
                Executor executor = Executor.getExecutorForScript(script);

                File jobOutputDir = new File("outputFiles/" + job.getId());
                File outputDir = executor.execute(SCRIPTS_DIR, new File(currentInputFile), script, jobOutputDir);

                File[] outputFiles = outputDir.listFiles();
                if (outputFiles != null && outputFiles.length > 0) {
                    File outputFile = outputFiles[0];
                    job.setStatus(PipelineJob.JobStatus.COMPLETED);
                    job.setOutputFile(outputFile.getAbsolutePath());
                    currentInputFile = outputFile.getAbsolutePath();
                } else {
                    job.setStatus(PipelineJob.JobStatus.FAILED);
                    pipeline.setStatus(Pipeline.PipelineStatus.FAILED);
                    break;
                }

                webSocketService.sendJobStatusUpdate(pipeline.getId(), job.getId(), job.getScript().getScript_id(), job.getScript().getName(), job.getStatus());
                pipelineRepository.save(pipeline);
            }
            webSocketService.sendPipelineStatusUpdate(pipeline.getId(), Pipeline.PipelineStatus.COMPLETED);
            updatePipelineStatus(pipeline);
            pipelineRepository.save(pipeline);
        });
        return pipelineResponse;
    }


    protected String saveInitialInputFile(MultipartFile file, long userId) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Le fichier d'entrée est nul ou vide");
        }

        Path directoryPath = Paths.get("inputFiles", String.valueOf(userId)).toAbsolutePath();
        File dir = directoryPath.toFile();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Impossible de créer le répertoire pour les fichiers d'entrée");
            }
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IOException("Nom de fichier invalide");
        }

        File savedFile = new File(dir, originalFilename);
        System.out.println("chemin absolu du fichier enregistré : " + savedFile.getAbsolutePath());

        try (InputStream inputStream = file.getInputStream();
             OutputStream outputStream = new FileOutputStream(savedFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("Fichier sauvegardé avec succès à : " + savedFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde du fichier : " + e.getMessage());
            throw e;
        }

        return savedFile.getAbsolutePath();
    }


    public void updatePipelineStatus(Pipeline pipeline) {
        if (pipeline.getJobs().stream().allMatch(job -> job.getStatus() == PipelineJob.JobStatus.COMPLETED)) {
            pipeline.setStatus(Pipeline.PipelineStatus.COMPLETED);
        } else if (pipeline.getJobs().stream().anyMatch(job -> job.getStatus() == PipelineJob.JobStatus.FAILED)) {
            pipeline.setStatus(Pipeline.PipelineStatus.FAILED);
        } else {
            pipeline.setStatus(Pipeline.PipelineStatus.RUNNING);
        }
    }

    public Pipeline findPipelineById(long id) {
        var pipeline = pipelineRepository.findById(id);
        return pipeline.orElse(null);
    }

    public List<PipelineJobResponse> findJobs(Long id) {
        var optional = pipelineRepository.findById(id);
        if(optional.isEmpty()) return List.of();
        var pipeline = optional.get();
        return pipeline.getJobs()
                .stream()
                .map(j -> new PipelineJobResponse(j.getId(), j.getScript().getName(), j.getInputFile(), j.getOutputFile(), j.getStatus()))
                .toList();
    }

    public Path findJobOutput(Long jobId) {
        var job = pipelineJobRepository.findById(jobId);
        if(job.isEmpty()) return null;
        return Paths.get(job.get().getOutputFile());
    }

    public List<PipelineResponse> findPipelinesByUser(User user) {
        return pipelineRepository
                .findByUser(user)
                .stream()
                .map(p -> new PipelineResponse(p.getId(), p.getName(), p.getStatus()))
                .toList();
    }
}
