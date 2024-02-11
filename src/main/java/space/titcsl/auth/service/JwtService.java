package space.titcsl.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import space.titcsl.auth.entity.User;

import java.util.HashMap;
import java.util.Map;

public interface JwtService {
    String generateToken(UserDetails userDetails);
    String extractUsername(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

    String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails);
}
