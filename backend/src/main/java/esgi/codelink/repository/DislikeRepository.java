package esgi.codelink.repository;

import esgi.codelink.entity.Dislike;
import esgi.codelink.entity.DislikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DislikeRepository extends JpaRepository<Dislike, DislikeId> {

}
