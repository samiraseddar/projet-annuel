package esgi.codelink.repository;

import esgi.codelink.entity.User;
import esgi.codelink.entity.script.Script;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScriptRepository extends JpaRepository<Script, Long> {
    List<Script> findByUser(User user);
}