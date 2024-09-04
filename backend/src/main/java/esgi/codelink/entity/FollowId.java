package esgi.codelink.entity;

import java.io.Serializable;
import java.util.Objects;

public class FollowId implements Serializable {

    private User follower;
    private User followed;

    public FollowId() {}

    public FollowId(User follower, User followed) {
        this.follower = Objects.requireNonNull(follower);
        this.followed = Objects.requireNonNull(followed);
    }

    public User getFollower() {
        return follower;
    }

    public void setFollower(User follower) {
        this.follower = Objects.requireNonNull(follower);
    }

    public User getFollowed() {
        return followed;
    }

    public void setFollowed(User followed) {
        this.followed = Objects.requireNonNull(followed);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        FollowId followId = (FollowId) o;
        return Objects.equals(follower, followId.follower) && Objects.equals(followed, followId.followed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(follower, followed);
    }

    @Override
    public String toString() {
        return "FollowId{" +
                "follower=" + follower +
                ", followed=" + followed +
                "}";
    }

}
