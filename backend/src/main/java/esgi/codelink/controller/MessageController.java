package esgi.codelink.controller;

import esgi.codelink.entity.Message;
import esgi.codelink.entity.User;
import esgi.codelink.service.MessageService;
import esgi.codelink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    @Autowired
    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestParam long senderId, @RequestParam long receiverId, @RequestBody Message message) {
        Message sentMessage = messageService.insert(message, senderId, receiverId);
        if (sentMessage != null) {
            return ResponseEntity.ok(sentMessage);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Message> updateMessage(@PathVariable long id, @RequestBody String newMessage) {
        Message updatedMessage = messageService.update(id, newMessage);
        if (updatedMessage != null) {
            return ResponseEntity.ok(updatedMessage);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteMessage(@RequestParam long senderId, @RequestParam long messageId) {
        boolean isDeleted = messageService.delete(senderId, messageId);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/conversation")
    public ResponseEntity<Set<Message>> getMessagesBetweenUsers(@RequestParam long userId1, @RequestParam long userId2) {
        User user1 = userService.findById(userId1);
        User user2 = userService.findById(userId2);
        if (user1 != null && user2 != null) {
            Set<Message> messages = messageService.findMsgBtwTwoUsers(user1, user2);
            return ResponseEntity.ok(messages);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Set<Message>> searchMessagesInConversation(@RequestParam long userId1, @RequestParam long userId2, @RequestParam String word) {
        User user1 = userService.findById(userId1);
        User user2 = userService.findById(userId2);
        if (user1 != null && user2 != null) {
            Set<Message> messages = messageService.findMsgInConversation(user1, user2, word);
            return ResponseEntity.ok(messages);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
