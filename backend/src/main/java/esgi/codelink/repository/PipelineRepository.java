package esgi.codelink.repository;

import esgi.codelink.entity.User;
import esgi.codelink.entity.pipeline.Pipeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PipelineRepository extends JpaRepository<Pipeline, Long> {

    List<Pipeline> findByUser(User user);
}
