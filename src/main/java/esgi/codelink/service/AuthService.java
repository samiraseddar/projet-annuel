package esgi.codelink.service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import java.io.IOException;
import esgi.codelink.repository.TokenRepository;
import esgi.codelink.repository.UserRepository;
import  esgi.codelink.entity.*;
import  esgi.codelink.dto.*;
@Service
public class AuthService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JpaUserDetailsService userDetailsService;

    private final TokenService tokenService;

    private final SecurityContextRepository securityContextRepository;

    private final SecurityContextHolderStrategy securityContextHolderStrategy;

    @Autowired
    public AuthService(TokenRepository tokenRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JpaUserDetailsService userDetailsService, TokenService tokenService, SecurityContextRepository securityContextRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
        this.securityContextRepository = securityContextRepository;
        this.securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    }



    public RegisterResponseDTO register(RegisterDTO registerDTO) throws IOException {
        if(userRepository.findByMail(registerDTO.getMail()).isPresent()){
            return new RegisterResponseDTO("Error", "Email already used");
        }

        var user = new User();
        user.setMail(registerDTO.getMail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        userRepository.save(user);
        return new RegisterResponseDTO("Success", "");
    }

    @Transactional
    public LoginResponseDTO restLogin(LoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response) {
        if(loginDTO.getPassword() == null || loginDTO.getPassword().isBlank()) {
            return new LoginResponseDTO("Password cannot be empty or null");
        }
        var user = userRepository.findByUsername(loginDTO.getMail());
        var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken( user.isEmpty() ? loginDTO.getMail() : user.get().getMail(), loginDTO.getPassword()));

        if(!authentication.isAuthenticated()) {
            return new LoginResponseDTO("Failed");
        }

        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextRepository.saveContext(context, request, response);

        var userDetails = (CustomUserDetails) authentication.getPrincipal();

        var jwt = tokenService.generateToken(userDetails);
        revokeAllTokens(userDetails);
        saveUserToken(userDetails.getUser(), jwt);

        return new LoginResponseDTO(
                userDetails.getUserId(),
                "Success",
                jwt,
                userDetails.getUser().getNbFollowers(),
                userDetails.getUser().getNbFollowing(),
                userDetails.getUser().getNbPosts()
        );
    }

    private void revokeAllTokens(CustomUserDetails user) {
        var validTokens = tokenRepository.findAllValidTokenByUser(user.getUserId());
        if(validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });

        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(User user, String jwt) {
        tokenRepository.save(new Token(jwt, user));
    }

    public String mvcLogin(LoginDTO userDTO, HttpServletRequest request, HttpServletResponse response) {
        if(userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            return "Password cannot be empty or null";
        }

        var token = UsernamePasswordAuthenticationToken.unauthenticated(userDTO.getMail(), userDTO.getPassword());

        try {
            var authentication = authenticationManager.authenticate(token);

            if(!authentication.isAuthenticated()) {
                return "Failed";
            }

            var context =  securityContextHolderStrategy.createEmptyContext();
            context.setAuthentication(authentication);
            securityContextHolderStrategy.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            return "Success";
        } catch (BadCredentialsException bce) {
            return "Ident error";
        }
    }
    public UserDetails current() {
        var context = SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();

        try {
            return (CustomUserDetails) authentication.getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }


    public UserDetails extractFromToken(String token) {
        var mail = tokenService.extractMail(token);
        return userDetailsService.loadUserByUsername(mail);
    }
    @Transactional
    public boolean restLogout(String token) {
        if (token == null ||!token.startsWith("Bearer ")) {
            return false;
        }
        var jwt = token.substring(7);
        var storedToken = tokenRepository.findByToken(jwt).orElse(null);

        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            SecurityContextHolder.clearContext();
            return true;
        }

        return false;
    }
}


