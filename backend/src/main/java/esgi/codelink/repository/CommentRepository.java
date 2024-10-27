package esgi.codelink.repository;

import esgi.codelink.entity.Comment;
import esgi.codelink.entity.script.Script;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.script.script_id = :scriptId")
    List<Comment> findByScriptId(@Param("scriptId") Long scriptId);
}