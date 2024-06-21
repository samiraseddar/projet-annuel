package esgi.codelink.repository;
import esgi.codelink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByMail(String mail);

    @Query(value = """
      select u from User u inner join Token t\s
      on t.user.userId = u.userId\s
      where t.token = :monToken and (t.expired = false or t.revoked = false)\s
      """)
    Optional<User> findUserByToken(String monToken);
}

