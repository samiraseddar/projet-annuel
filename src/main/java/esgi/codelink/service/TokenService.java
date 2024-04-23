package esgi.codelink.service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import esgi.codelink.entity.CustomUserDetails;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class TokenService  {

    /**
     * The secret key used for JWT signing and validation.
     */
    private static final String SECRET_KEY ="pKesIhBahvzFRRxF2bxXsZ7xO5JwvDGj253Tc4PyOFl97bo8qo8T0ujlAlLWHIgL";

    /**
     * Generates a JWT token for the specified user details.
     *
     * @param userDetails the user details for which the token is generated
     * @return the generated JWT token
     */
    public String generateToken(CustomUserDetails userDetails) {
        return generateKey(Map.of("username", userDetails.getMail()), userDetails);
    }

    /**
     * Generates a JWT token with extra claims based on the provided user details.
     *
     * @param extraClaims  additional claims to include in the token
     * @param userDetails  the user details for which the token is generated
     * @return the generated JWT token
     */
    private String generateKey(Map<String, Object> extraClaims, CustomUserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the subject (mail) from the provided JWT token.
     *
     * @param token the JWT token from which to extract the subject
     * @return the subject (mail) extracted from the token
     */
    public String extractMail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the provided JWT token.
     *
     * @param token          the JWT token from which to extract the claim
     * @param claimsResolver the function to resolve the desired claim
     * @param <T>            the type of the claim
     * @return the extracted claim
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the provided JWT token.
     *
     * @param token the JWT token from which to extract the claims
     * @return all claims extracted from the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Retrieves the signing key for JWT validation.
     *
     * @return the signing key
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Checks if the provided JWT token is valid for the specified user details.
     *
     * @param token       the JWT token to validate
     * @param userDetails the user details against which to validate the token
     * @return {@code true} if the token is valid, {@code false} otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String mail = extractMail(token);
        return mail.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Checks if the provided JWT token has expired.
     *
     * @param token the JWT token to check for expiration
     * @return {@code true} if the token has expired, {@code false} otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the provided JWT token.
     *
     * @param token the JWT token from which to extract the expiration date
     * @return the expiration date extracted from the token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}