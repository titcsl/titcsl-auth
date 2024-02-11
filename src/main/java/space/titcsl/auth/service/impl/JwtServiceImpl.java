package space.titcsl.auth.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import space.titcsl.auth.exception.GlobalExceptionHandler;
import space.titcsl.auth.service.JwtService;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${space.titcsl.auth.access_secret_key}")
    private String secret_key;

    public String generateToken(UserDetails userDetails){
        return Jwts.builder().setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setIssuer("TEREDESAI INFORMATION TECHNOLOGY CONSULTANCY SERVICE LIMITED.")
                .setExpiration(new Date(System.currentTimeMillis() + 1814400000))
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return  Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1814400000))
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaim(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) );
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private Claims extractAllClaim(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(getSigninKey()).build().parseClaimsJws(token).getBody();
        } catch (SecurityException e) {

            throw new GlobalExceptionHandler("We have locked account for internal server security bits exception for securing it more. Please login again! Sorry for inconvenience");
        } catch (Exception e) {

            throw new GlobalExceptionHandler("We have locked account for internal server security bits exception for securing it more. Please login again! Sorry for inconvenience");
        }
    }
    private Key getSigninKey() {
        byte[] key = Decoders.BASE64.decode(secret_key);
        return Keys.hmacShaKeyFor(key);
    }


}
