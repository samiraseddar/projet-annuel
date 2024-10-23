package esgi.codelink.controller;
import esgi.codelink.dto.*;
import esgi.codelink.entity.CustomUserDetails;
import esgi.codelink.service.AuthService;
import esgi.codelink.service.FollowService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import esgi.codelink.entity.User;
import esgi.codelink.service.UserService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public UserController(UserService userService,AuthService authService) {
        this.userService = userService;
        this.authService=authService;
    }
    @PostMapping("/test-request")
    public ResponseEntity<String> testPostRequest() {
        return ResponseEntity.ok("POST request successful");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable long userId) {
        User user = userService.findById(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/signUp")
    public ResponseEntity<RegisterResponseDTO> signUp(@RequestBody RegisterDTO registerDTO) throws IOException {
        var res = authService.register(registerDTO);
        if(res.getStatus().equals("Success")) {
            return ResponseEntity.ok(res);
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

        }

    }
    @PostMapping("/signIn")
    public ResponseEntity<LoginResponseDTO> signIn(@RequestBody LoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response) throws IOException {
        var res = authService.login(loginDTO,request,response);
        if(res.getStatus().equals("Success")) {
            return ResponseEntity.ok(res);
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

    }


    @PostMapping("/{followeeId}/follows")
    public ResponseEntity<Void> follow(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable long followeeId) {
        System.out.println("CONTROLLER FOLLOW ");
        boolean success = userService.followUser(userDetails.getUserId(), followeeId);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{followeeId}/follows")
    public ResponseEntity<Void> unfollow(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable long followeeId) {
        System.out.println("CONTROLLER UNFOLLOW");
        boolean success = userService.unfollowUser(userDetails.getUserId(), followeeId);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/{followerId}/follow/{followeeId}")
    public ResponseEntity<Void> followUser(@PathVariable long followerId, @PathVariable long followeeId) {
        boolean success = userService.followUser(followerId, followeeId);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{followerId}/unfollow/{followeeId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable long followerId, @PathVariable long followeeId) {
        boolean success = userService.unfollowUser(followerId, followeeId);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        List<User> users = userService.searchUsers(query);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<User>> getFollowers(@PathVariable Long userId) {
        List<User> followers = userService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<User>> getFollowing(@PathVariable Long userId) {
        List<User> following = userService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/isFollowing/{userId}")
    public ResponseEntity<Boolean> isFollowing(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long userId) {
        User currentUser = userDetails.getUser();
        return ResponseEntity.ok(userService.isFollowing(currentUser, userId));
    }
}