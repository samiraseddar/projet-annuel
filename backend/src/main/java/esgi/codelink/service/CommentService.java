package esgi.codelink.service;

import esgi.codelink.dto.CommentDTO;
import esgi.codelink.entity.Comment;
import esgi.codelink.entity.User;
import esgi.codelink.entity.script.Script;
import esgi.codelink.repository.CommentRepository;
import esgi.codelink.repository.ScriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ScriptRepository scriptRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, ScriptRepository scriptRepository) {
        this.commentRepository = commentRepository;
        this.scriptRepository = scriptRepository;
    }

    @Transactional
    public Comment addComment(CommentDTO commentDTO, long scriptId, User user) {
        var optionalScript = scriptRepository.findById(scriptId);
        if(optionalScript.isEmpty()) return null;
        var script = optionalScript.get();

        Comment comment = commentDTO.convertToComment();
        comment.setScript(script);
        comment.setUser(user);

        return commentRepository.save(comment);
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