package esgi.codelink.service;

import esgi.codelink.dto.CommentDTO;
import esgi.codelink.entity.Comment;
import esgi.codelink.entity.User;
import esgi.codelink.entity.script.Script;
import esgi.codelink.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Transactional
    public Comment addComment(CommentDTO commentDTO, Script script, User user) {
        Comment comment = commentDTO.convertToComment();
        comment.setScript(script);
        comment.setUser(user);

        var newComment = commentRepository.save(comment);
        System.out.println("comment to add : " + comment);
        return newComment;
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public List<Comment> getCommentsByScript(Long scriptId) {
        return commentRepository.findByScriptId(scriptId);
    }

    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }
}