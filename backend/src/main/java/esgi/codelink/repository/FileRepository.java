package esgi.codelink.repository;

import esgi.codelink.entity.script.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
//import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByUserUserId(Long userId);
    List<File> findByUserUserIdAndIsGenerated(Long userId, boolean isGenerated);
}
