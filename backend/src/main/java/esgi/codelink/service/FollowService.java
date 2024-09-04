package esgi.codelink.service;

import esgi.codelink.entity.Follow;
import esgi.codelink.entity.FollowId;
import esgi.codelink.entity.User;
import esgi.codelink.repository.FollowRepository;
import esgi.codelink.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    private final EntityManager em;

    @Autowired
    public FollowService(FollowRepository followRepository, UserRepository userRepository, EntityManager em) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.em = em;
    }


    @Transactional
    public Follow insert(long userFollower, long userFollowed) {
        var follower = em.find(User.class, userFollower, LockModeType.PESSIMISTIC_WRITE);
        var followed = em.find(User.class, userFollowed, LockModeType.PESSIMISTIC_WRITE);

        if (followed == null || follower == null) return null;

        follower.incrementFollowing();
        followed.incrementFollowers();

        return followRepository.save(new Follow(follower, followed));
    }


    @Transactional
    public Follow findById(long userFollower, long userFollowed) {
        var follower = userRepository.findById(userFollower);
        var followed = userRepository.findById(userFollowed);

        if (followed.isEmpty() || follower.isEmpty()) return null;

        var id = new FollowId(follower.get(), followed.get());
        return followRepository.findById(id).orElse(null);
    }


    @Transactional
    public boolean delete(long userFollower, long userFollowed) {
        var follower = em.find(User.class, userFollower, LockModeType.PESSIMISTIC_WRITE);
        var followed = em.find(User.class, userFollowed, LockModeType.PESSIMISTIC_WRITE);

        if (followed == null || follower == null) return false;

        follower.decrementFollowing();
        followed.decrementFollowers();

        userRepository.save(follower);
        userRepository.save(followed);

        var id = new FollowId(follower, followed);
        followRepository.deleteById(id);
        return true;
    }
}