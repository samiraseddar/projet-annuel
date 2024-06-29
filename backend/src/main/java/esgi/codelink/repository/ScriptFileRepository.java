package esgi.codelink.repository;

import esgi.codelink.entity.script.ScriptFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScriptFileRepository extends JpaRepository<ScriptFile, Long> {

    ScriptFile getScriptFileById(Long id);

    @Query(value = """
      select sc from ScriptFile sc inner join User u\s
      on sc.user.userId = u.userId\s
      where sc.user.userId = :userId\s
      """)
    List<ScriptFile> findByUserId(Long userId);
}
