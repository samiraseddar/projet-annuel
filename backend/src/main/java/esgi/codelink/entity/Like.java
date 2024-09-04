package esgi.codelink.entity;

import esgi.codelink.entity.script.Script;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name="likes")
@IdClass(LikeId.class)
public class Like {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Script script;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Like(){}

    public Like(Script script, User user) {
        this.script = Objects.requireNonNull(script);
        this.user = Objects.requireNonNull(user);
    }

    public Script getScript() {
        return script;
    }

    public void setPost(Script script) {
        this.script = Objects.requireNonNull(script);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Like like = (Like) o;
        return Objects.equals(script, like.script) &&
                Objects.equals(user, like.user);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Like{" +
                "script=" + script +
                ", user=" + user +
                '}';
    }
}
