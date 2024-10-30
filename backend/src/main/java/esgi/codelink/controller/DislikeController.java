package esgi.codelink.controller;


import esgi.codelink.entity.CustomUserDetails;
import esgi.codelink.service.DislikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class DislikeController {

    private final DislikeService service;

    @Autowired
    public DislikeController(DislikeService service) {
        this.service = service;
    }


    @PostMapping("/api/dislikes/{scriptId}")
    public ResponseEntity<Boolean> addDislike(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable long scriptId) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var dislike = service.insert(userDetails.getUserId(), scriptId);
        return dislike == null ? ResponseEntity.ok(false) : ResponseEntity.ok(true);
    }


    @DeleteMapping("/api/dislikes/{scriptId}")
    public ResponseEntity<Void> deleteDislike(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable long scriptId) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return service.delete(userDetails.getUserId(), scriptId) ? ResponseEntity.status(HttpStatus.OK).build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
