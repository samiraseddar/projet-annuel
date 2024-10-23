package esgi.codelink.entity;

import esgi.codelink.entity.script.Script;
import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    private String content;

    @ManyToOne
    @JoinColumn(name = "script_id", nullable = false)
    private Script script;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Comment() {}

    public Comment(String content, Script script, User user) {
        this.content = content;
        this.script = script;
        this.user = user;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", content='" + content + " " +
        ", script=" + script +
                '}';
    }
}