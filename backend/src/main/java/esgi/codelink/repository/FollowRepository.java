package esgi.codelink.repository;

import esgi.codelink.entity.Follow;
import esgi.codelink.entity.FollowId;
import esgi.codelink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    @Query("SELECT f.followed FROM Follow f WHERE f.follower = :user")
    Set<User> findFollowedUser(User user);

    @Query("SELECT f.follower FROM Follow f WHERE f.followed = :user")
    Set<User> findFollowerUser(User user);
}