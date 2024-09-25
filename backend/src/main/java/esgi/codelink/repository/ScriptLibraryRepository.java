package esgi.codelink.repository;

import esgi.codelink.entity.ScriptLibrary;
import esgi.codelink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScriptLibraryRepository extends JpaRepository<ScriptLibrary, Long> {

    List<ScriptLibrary> findAllByOwner(User owner);  // Corrected to match the attribute name in ScriptLibrary
}