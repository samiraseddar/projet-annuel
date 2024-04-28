package esgi.codelink.config;
import esgi.codelink.service.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import esgi.codelink.repository.*;
@Component
public class AuthenticationFilter extends OncePerRequestFilter { // pour appler la requette

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    private final SecurityContextRepository securityContextRepository;

    @Autowired // a chaques fois que je mis un service ou bien a repositorie ...
    public AuthenticationFilter(TokenService tokenService, @Lazy  TokenRepository tokenRepository, @Lazy UserDetailsService userDetailsService, @Lazy SecurityContextRepository securityContextRepository) {
        this.tokenService= tokenService;  //pour vérifier si elle a sun token de connexion
        this.userDetailsService = userDetailsService;
        this.tokenRepository = tokenRepository;
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().contains("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("BEARER ")) {
            filterChain.doFilter(request, response);
            return;
        }

        var jwt = authHeader.substring(7);
        var mail = tokenService.extractMail(jwt);
        if (mail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var user = userDetailsService.loadUserByUsername(mail);
            var token = tokenRepository.findByToken(jwt);
            if (tokenService.isTokenValid(jwt, user) && token.isPresent() && !token.get().isRevoked()) {
                if (token.get().isExpired()) {
                    response.setStatus(401);
                    filterChain.doFilter(request, response);
                    return;
                }
                var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                var context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authToken);
                securityContextRepository.saveContext(context, request, response);
            }
        }
        filterChain.doFilter(request, response);
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }
}