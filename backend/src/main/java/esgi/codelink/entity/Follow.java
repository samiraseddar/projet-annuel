package esgi.codelink.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@IdClass(FollowId.class)
public class Follow {

    @Id
    @ManyToOne
    private User follower;

    @Id
    @ManyToOne
    private User followed;

    public Follow() {
    }

    public Follow(User follower, User followed) {
        this.follower = Objects.requireNonNull(follower);
        this.followed = Objects.requireNonNull(followed);
    }


    public User getFollower() {
        return follower;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public User getFollowed() {
        return followed;
    }

    public void setFollowed(User followed) {
        this.followed = Objects.requireNonNull(followed);
    }

    @Override
    public String toString() {
        return "Follow{" +
                "follower=" + follower +
                ", followed=" + followed +
                "}";
    }
}
