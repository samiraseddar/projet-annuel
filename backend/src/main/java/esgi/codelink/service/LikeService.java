package esgi.codelink.service;

import esgi.codelink.entity.Like;
import esgi.codelink.entity.LikeId;
import esgi.codelink.entity.script.Script;
import esgi.codelink.repository.LikeRepository;
import esgi.codelink.repository.ScriptRepository;
import esgi.codelink.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final ScriptRepository scriptRepository;
    private final UserRepository userRepository;

    @PersistenceContext
    private final EntityManager em;


    @Autowired
    public LikeService(LikeRepository repository, ScriptRepository scriptRepository,
                       UserRepository userRepository, EntityManager em) {
        this.likeRepository = repository;
        this.scriptRepository = scriptRepository;
        this.userRepository = userRepository;
        this.em = em;
    }


    @Transactional
    public Like insert(long userId, long scriptId) {
        var user = userRepository.findById(userId);
        var script = em.find(Script.class, scriptId, LockModeType.PESSIMISTIC_WRITE);

        if(user.isEmpty() || script == null) return null;

        var like = likeRepository.findById(new LikeId(script, user.get()));
        if(like.isPresent()) return like.get();

        var newLike = new Like(script, user.get());
        script.incrementLikes();
        scriptRepository.save(script);
        return likeRepository.save(newLike);
    }


    @Transactional
    public boolean delete(long userId, long scriptId){
        var user = userRepository.findById(userId);
        var post = scriptRepository.findById(scriptId);
        if(user.isEmpty() || post.isEmpty()) return false;

        var id = new LikeId(post.get(), user.get());
        var like = likeRepository.findById(id);
        if(like.isPresent()) {
            var script = post.get();
            script.decrementLikes();
            scriptRepository.save(script);
            likeRepository.delete(like.get());
            return true;
        }
        return false;
    }


    @Transactional
    public Like findById(long userId, long scriptId) {
        var script = scriptRepository.findById(scriptId);
        var user = userRepository.findById(userId);

        if (script.isEmpty() || user.isEmpty()) return null;

        var id = new LikeId(script.get(), user.get());
        return likeRepository.findById(id).orElse(null);
    }
}