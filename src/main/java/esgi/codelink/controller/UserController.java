package esgi.codelink.controller;
import esgi.codelink.dto.LoginDTO;
import esgi.codelink.dto.LoginResponseDTO;
import esgi.codelink.dto.RegisterDTO;
import esgi.codelink.dto.RegisterResponseDTO;
import esgi.codelink.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import esgi.codelink.entity.User;
import esgi.codelink.service.UserService;

import java.io.IOException;

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
        System.out.println(registerDTO);
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





}