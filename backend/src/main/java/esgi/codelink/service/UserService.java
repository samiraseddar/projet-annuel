package esgi.codelink.service;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import  esgi.codelink.repository.UserRepository;
import  esgi.codelink.entity.User;
/**
 * Service class for managing user-related operations.
 */
@Service
public class UserService {

    private final UserRepository repository;

    /**
     * Constructs a UserService with the provided UserRepository.
     *
     * @param repository The repository for user-related operations.
     */
    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return The user with the specified ID, or null if not found.
     */
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

        return true;
    }
    @Transactional
    public boolean unfollowUser(long followerId, long followeeId) {
        User follower = findById(followerId);
        User followee = findById(followeeId);

        if (follower == null || followee == null) {
            return false;
        }

        follower.decrementFollowing();
        followee.decrementFollowers();

        repository.save(follower);
        repository.save(followee);

        return true;
    }
}







