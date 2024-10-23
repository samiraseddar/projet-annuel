package esgi.codelink.repository;
import esgi.codelink.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByMail(String mail);

    @Query("SELECT u FROM User u where u.firstName like %:keyword% or u.lastName like %:keyword%")
    List<User> searchUser(String keyword);
}