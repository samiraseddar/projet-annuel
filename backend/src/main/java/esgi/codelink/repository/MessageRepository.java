package esgi.codelink.repository;

import esgi.codelink.entity.Message;
import esgi.codelink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface MessageRepository extends JpaRepository<Message, MsgId> {
   // pour donner les message entre 2 personnne
    @Query("SELECT m FROM Message m WHERE (m.receiver = :u1 and m.sender = :u2) or (m.receiver = :u2 and m.sender = :u1) ORDER BY m.timestamp")
    Set<Message> findMsgBtwTwoUsers(User u1, User u2);
    // pour faire un filtre pour chercher un message .
    @Query("SELECT m FROM Message m WHERE ((m.receiver = :u1 and m.sender = :u2) or (m.receiver = :u2 and m.sender = :u1)) and m.msg LIKE %:word% ORDER BY m.timestamp")
    Set<Message> findMsgInConversation(User u1, User u2, String word);
}
