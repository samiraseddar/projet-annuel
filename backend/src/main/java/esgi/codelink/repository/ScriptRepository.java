package esgi.codelink.repository;

import esgi.codelink.entity.script.Script;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScriptRepository extends JpaRepository<Script, Long> {
}