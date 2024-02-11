package space.titcsl.auth.service;

import org.springframework.http.ResponseEntity;
import space.titcsl.auth.dto.JwtAuthenticationResponse;
import space.titcsl.auth.dto.LoginRequest;
import space.titcsl.auth.dto.RefreshTokenRequest;
import space.titcsl.auth.dto.RegisterRequest;
import space.titcsl.auth.entity.User;

public interface AuthenticationService {
    ResponseEntity<?> register(RegisterRequest register);
    JwtAuthenticationResponse login(LoginRequest login);
    JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
