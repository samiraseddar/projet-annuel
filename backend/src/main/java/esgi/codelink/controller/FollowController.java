package esgi.codelink.controller;

import esgi.codelink.entity.CustomUserDetails;
import esgi.codelink.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class FollowController {

    private final FollowService service;

    @Autowired
    public FollowController(FollowService service) {
        this.service = service;
    }


    @GetMapping("api/follows/{followedId}")
    public ResponseEntity<Boolean> getFollowed(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable long followedId) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var follow = service.findById(userDetails.getUserId(), followedId);
        return follow == null ? ResponseEntity.ok(false) : ResponseEntity.ok(true);
    }


    @PostMapping("api/follows/{followedId}")
    public ResponseEntity<Boolean> addFollow(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable long followedId) {
        if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (userDetails.getUserId() == followedId) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        var follow = service.insert(userDetails.getUserId(), followedId);
        return follow == null ? ResponseEntity.ok(false) : ResponseEntity.ok(true);
    }


    @DeleteMapping("api/follows/{followedId}")
    public ResponseEntity<Void> deleteFollow(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable long followedId) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return service.delete(userDetails.getUserId(), followedId) ? ResponseEntity.status(HttpStatus.OK).build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}


