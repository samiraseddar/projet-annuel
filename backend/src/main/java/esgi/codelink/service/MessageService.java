package esgi.codelink.service;

import esgi.codelink.entity.*;
import esgi.codelink.repository.MessageRepository;
import esgi.codelink.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
public class MessageService {

    private final MessageRepository repository;

    private final UserRepository userRepository;

    @Autowired
    public MessageService(MessageRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Message insert(Message message, long senderId, long receiverId) {
        Objects.requireNonNull(message);
        var sender = userRepository.findById(senderId);
        var receiver = userRepository.findById(receiverId);

        if(sender.isEmpty() || receiver.isEmpty()) return null;

        message.setSender(sender.get());
        message.setReceiver(receiver.get());

        return repository.save(message);
    }

    @Transactional
    public Message update(long id, String newMessage) {
        Objects.requireNonNull(newMessage);
        var opt = repository.findById(id);

        if(opt.isEmpty()) return null;

        var message = opt.get();
        message.setMsg(newMessage);

        return repository.save(message);
    }


    @Transactional
    public boolean delete(long senderId, long id) {
        var sender = userRepository.findById(senderId);
        if(sender.isEmpty()) return false;

        var message = repository.findById(id);
        if(message.isEmpty()) return false;

        if(!message.get().getSender().equals(sender.get())) return false;
        repository.deleteById(id);
        return true;
    }
    @Transactional
    public Set<Message> findMsgBtwTwoUsers(User u1, User u2) {
        return repository.findMsgBtwTwoUsers(u1, u2);
    }

    @Transactional
    public Set<Message> findMsgInConversation(User u1, User u2, String word) {
        return repository.findMsgInConversation(u1, u2, word);
    }
}
