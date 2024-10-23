package esgi.codelink.entity;

import esgi.codelink.entity.script.Script;

import java.io.Serializable;
import java.util.Objects;


public class DislikeId implements Serializable {

    private Script script;
    private User user;

    public DislikeId() {}

    public DislikeId(Script script, User user) {
        this.script = script;
        this.user = user;
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
        DislikeId dislikeId = (DislikeId) o;
        return Objects.equals(script, dislikeId.script) && Objects.equals(user, dislikeId.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(script, user);
    }
}
