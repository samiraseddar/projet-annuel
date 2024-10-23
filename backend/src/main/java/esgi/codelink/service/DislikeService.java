package esgi.codelink.service;

import esgi.codelink.entity.Dislike;
import esgi.codelink.entity.DislikeId;
import esgi.codelink.entity.Like;
import esgi.codelink.entity.LikeId;
import esgi.codelink.entity.script.Script;
import esgi.codelink.repository.DislikeRepository;
import esgi.codelink.repository.ScriptRepository;
import esgi.codelink.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DislikeService {

    private final DislikeRepository dislikeRepository;
    private final ScriptRepository scriptRepository;
    private final UserRepository userRepository;

    @PersistenceContext
    private final EntityManager em;


    @Autowired
    public DislikeService(DislikeRepository dislikeRepository, ScriptRepository scriptRepository,
                          UserRepository userRepository, EntityManager em) {
        this.dislikeRepository = dislikeRepository;
        this.scriptRepository = scriptRepository;
        this.userRepository = userRepository;
        this.em = em;
    }


    @Transactional
    public Dislike insert(long userId, long scriptId) {
        var user = userRepository.findById(userId);
        var script = em.find(Script.class, scriptId, LockModeType.PESSIMISTIC_WRITE);

        if(user.isEmpty() || script == null) return null;

        var dislike = dislikeRepository.findById(new DislikeId(script, user.get()));
        if(dislike.isPresent()) return dislike.get();

        var newDislike = new Dislike(script, user.get());
        script.incrementDislikes();
        scriptRepository.save(script);
        return dislikeRepository.save(newDislike);
    }


    @Transactional
    public boolean delete(long userId, long scriptId){
        var user = userRepository.findById(userId);
        var post = scriptRepository.findById(scriptId);
        if(user.isEmpty() || post.isEmpty()) return false;

        var id = new DislikeId(post.get(), user.get());
        var dislike = dislikeRepository.findById(id);
        if(dislike.isPresent()) {
            var script = post.get();
            script.decrementDislikes();
            scriptRepository.save(script);
            dislikeRepository.delete(dislike.get());
            return true;
        }
        return false;
    }


    @Transactional
    public Dislike findById(long userId, long scriptId) {
        var script = scriptRepository.findById(scriptId);
        var user = userRepository.findById(userId);

        if (script.isEmpty() || user.isEmpty()) return null;

        var id = new DislikeId(script.get(), user.get());
        return dislikeRepository.findById(id).orElse(null);
    }
}