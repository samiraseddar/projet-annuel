package esgi.codelink.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import  esgi.codelink.entity.Token;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> { // la c'est pour chercher les token valide
    @Query(value = """
      select t from Token t inner join User u\s
      on t.user.userId = u.userId\s
      where u.userId = :id and (t.expired = false or t.revoked = false)\s
      """)
    List<Token> findAllValidTokenByUser(Long id); // j'ai fait une requette

    @Query( value = """
    SELECT t FROM Token t\s
    WHERE t.token = ':monToken' and (t.expired = false or t.revoked = false)\s
    """)
    Optional<Token> findByToken(String monToken);
}
