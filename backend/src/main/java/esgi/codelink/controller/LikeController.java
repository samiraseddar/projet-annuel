package esgi.codelink.controller;

import esgi.codelink.entity.CustomUserDetails;
import esgi.codelink.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class LikeController {

    private final LikeService service;

    @Autowired
    public LikeController(LikeService service) {
        this.service = service;
    }


    @PostMapping("/api/likes/{scriptId}")
    public ResponseEntity<Boolean> addLike(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable long scriptId) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var like = service.insert(userDetails.getUserId(), scriptId);
        return like == null ? ResponseEntity.ok(false) : ResponseEntity.ok(true);
    }


    @DeleteMapping("/api/likes/{scriptId}")
    public ResponseEntity<Void> deleteLike(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable long scriptId) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return service.delete(userDetails.getUserId(), scriptId) ? ResponseEntity.status(HttpStatus.OK).build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
