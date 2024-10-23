package esgi.codelink.repository;

import esgi.codelink.entity.Comment;
import esgi.codelink.entity.script.Script;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByScript(Script script);
}