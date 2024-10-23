package esgi.codelink.repository;

import esgi.codelink.entity.Like;
import esgi.codelink.entity.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, LikeId> {

}

