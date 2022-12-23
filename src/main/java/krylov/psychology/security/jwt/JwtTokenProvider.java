package krylov.psychology.security.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
@Component
public class JwtTokenProvider {
    @Value("${adminPassword}")
    private String adminPassword;
    @Value("${adminName}")
    private String adminName;
    @Value("${jwt-token-secret}")
    private String secret;
    @Value("${jwt-token-expired}")
    private long livingTime;

    public String createToken(String login) {
        Claims claims = Jwts.claims().setSubject(login);
//        claims.put("roles", some_role)
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + livingTime);
        SecretKey signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(signingKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        String login = getUserLogin(token);
        UserDetails userDetails = JwtUserFactory.create(login);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserLogin(String token) {
        SecretKey signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        return Jwts.parserBuilder().setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean tokenIsValid(String token) throws JwtAuthenticationException {
        try {
            SecretKey signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
            Claims claimsJwt =
                    Jwts.parserBuilder().setSigningKey(signingKey)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();
            if (claimsJwt.getExpiration().before(new Date())) {
                System.out.println("Token NOT valid anymore!");
                return false;
            }
            System.out.println("Token is valid.");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JwtTokenProvider tokenIsValid error");
        }
    }
}
