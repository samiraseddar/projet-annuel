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
public class AuthService {//c'est le service qui gére tt l'eutentification (tttt)

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager; //une classe de spring sucuritée elle sert a enregistrée la connexion d'un etulisateur

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
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));//pour ne pas afficher on utulise un passeword Encoder
        userRepository.save(user);
        return new RegisterResponseDTO("Success", "");
    }

    @Transactional
    public LoginResponseDTO login(LoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response) {
        if(loginDTO.getPassword() == null || loginDTO.getPassword().isBlank()) {
            return new LoginResponseDTO("Password cannot be empty or null");
        }
        var user = userRepository.findByMail(loginDTO.getMail());
        var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken( user.isEmpty() ? loginDTO.getMail() : user.get().getMail(), loginDTO.getPassword()));
        //pour dire si il es connectée ou pas .....
        if(!authentication.isAuthenticated()) {
            return new LoginResponseDTO("Failed");
        }

        var context = SecurityContextHolder.createEmptyContext(); //ça stoque les details de securitée de tt l'application
        context.setAuthentication(authentication); //il vas permetre d'enregistrée l'eulisateur authentifier
        securityContextRepository.saveContext(context, request, response);

        var userDetails = (CustomUserDetails) authentication.getPrincipal();
        //spring securitée travail avec des userdetails
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

    private void revokeAllTokens(CustomUserDetails user) { //pour invalidée tt les tokes d'avant
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

    public UserDetails current() { //l'etulisateur qui es connectée en se moment
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
    public boolean logout(String token) {
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


