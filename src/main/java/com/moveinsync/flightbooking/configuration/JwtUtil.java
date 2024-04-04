package com.moveinsync.flightbooking.configuration;
import com.moveinsync.flightbooking.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String Secret = "ZBlt3w4fMYGh4pOzqxltO9XDPdSxITLQ2H8eOZUDdylZb6e5H6z5U6YyU9ukpveblODBCdBHm5bMkU5h5JmkY6iw";

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", user.getUsername());
        claims.put("exp", System.currentTimeMillis() + 1000 * 60 * 60 * 10);
        claims.put("iat", new Date().getTime());
        claims.put("isadmin", user.getisAdmin());

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, Secret)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(Secret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // log the exception
            return false;
        }
    }

    public String extractUsername(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(Secret).parseClaimsJws(token).getBody();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            // log the exception
            return null;
        }
    }


    public boolean isTokenExpired(String token) {
        return Jwts.parser().setSigningKey(Secret).parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }

    public boolean isAdmin(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(Secret).parseClaimsJws(token).getBody();
            Boolean isAdmin = (Boolean) claims.get("isadmin");
            return isAdmin != null && isAdmin;
        } catch (JwtException | IllegalArgumentException e) {
            // log the exception
            return false;
        }
    }


}
