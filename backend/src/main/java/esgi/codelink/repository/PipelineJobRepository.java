package esgi.codelink.repository;

import esgi.codelink.entity.pipeline.PipelineJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PipelineJobRepository extends JpaRepository<PipelineJob, Long> {

}
