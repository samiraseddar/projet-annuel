package esgi.codelink.service;

import esgi.codelink.entity.Follow;
import esgi.codelink.entity.FollowId;
import esgi.codelink.repository.FollowRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import  esgi.codelink.repository.UserRepository;
import  esgi.codelink.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing user-related operations.
 */
@Service
public class UserService {

    private final UserRepository repository;
    private final FollowRepository followRepository;


    @Autowired
    public UserService(UserRepository repository, FollowRepository followRepository) {
        this.repository = repository;
        this.followRepository = followRepository;
    }


    public User findById(long userId) {
        var user = repository.findById(userId);
        return user.orElse(null);
    }
    @Transactional
    public boolean followUser(long followerId, long followeeId) {
        User follower = findById(followerId);
        User followee = findById(followeeId);

        if (follower == null || followee == null) {
            return false;
        }

        follower.incrementFollowing();
        followee.incrementFollowers();

        repository.save(follower);
        repository.save(followee);

        var follow = new Follow(follower, followee);
        followRepository.save(follow);

        return true;
    }
    @Transactional
    public boolean unfollowUser(long followerId, long followeeId) {
        System.out.println("user service unfollowUser");
        User follower = findById(followerId);
        User followee = findById(followeeId);

        if (follower == null || followee == null) {
            return false;
        }

        follower.decrementFollowing();
        followee.decrementFollowers();

        repository.save(follower);
        repository.save(followee);

        var followId = new FollowId(follower, followee);
        followRepository.deleteById(followId);

        return true;
    }

    public List<User> searchUsers(String query) {
        return repository.searchUser(query);
    }

    public List<User> getFollowers(Long userId) {
        return followRepository.findFollowersByUserId(userId);
    }

    public List<User> getFollowing(Long userId) {
        return followRepository.findFollowingByUserId(userId);
    }

    public boolean isFollowing(User follower, Long followedUserId) {
        Optional<User> followedUserOpt = repository.findById(followedUserId);
        if (followedUserOpt.isEmpty()) {
            return false;
        }
        User followedUser = followedUserOpt.get();
        Optional<Follow> follow = followRepository.findByFollowerAndFollowed(follower, followedUser);
        return follow.isPresent();
    }
}