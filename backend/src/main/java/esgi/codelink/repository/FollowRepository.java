package esgi.codelink.repository;

import esgi.codelink.entity.Follow;
import esgi.codelink.entity.FollowId;
import esgi.codelink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    @Query("SELECT f.follower FROM Follow f WHERE f.followed.userId = :userId")
    List<User> findFollowersByUserId(Long userId);

    @Query("SELECT f.followed FROM Follow f WHERE f.follower.userId = :userId")
    List<User> findFollowingByUserId(Long userId);

    Optional<Follow> findByFollowerAndFollowed(User follower, User followed);
}