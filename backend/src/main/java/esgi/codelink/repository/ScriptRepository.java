package esgi.codelink.repository;

import esgi.codelink.entity.User;
import esgi.codelink.entity.script.Script;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScriptRepository extends JpaRepository<Script, Long> {
    List<Script> findByUser(User user);

    /*@Query("SELECT s FROM Script s WHERE s.user = :user OR s.protectionLevel = 'PUBLIC' OR s.user IN (SELECT f.follower FROM Follow f WHERE f.following = :user)")
    List<Script> findAllUsableScript(User user);*/
}