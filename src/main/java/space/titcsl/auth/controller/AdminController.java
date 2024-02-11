package space.titcsl.auth.controller;

import jakarta.persistence.NonUniqueResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import space.titcsl.auth.dto.JustDataDto;
import space.titcsl.auth.entity.LockAccountRequest;
import space.titcsl.auth.entity.User;
import space.titcsl.auth.exception.GlobalExceptionHandler;
import space.titcsl.auth.repository.LockAccountRepository;
import space.titcsl.auth.repository.UserRepository;
import space.titcsl.auth.service.AdminService;
import space.titcsl.auth.service.EmailService;
import space.titcsl.auth.service.JwtService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/${space.titcsl.auth.api.version}/admin")
public class AdminController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final LockAccountRepository lockAccountRepository;
    private final EmailService emailService;
    private final AdminService adminService;

    @PostMapping("/lockAccount")
    public ResponseEntity<?> lockAccount(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody JustDataDto data){
        String token = extractToken(authorizationHeader);
        String email = jwtService.extractUsername(token);
        try {
            return ResponseEntity.ok(adminService.lockAccount(data, email));
        }catch (GlobalExceptionHandler ex) {
            Map<String, String> response = new HashMap<>();
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(response);
        }
    }


    @PostMapping("/unlockAccount")
    public ResponseEntity<?> unlockAccount(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody JustDataDto data){
        String token = extractToken(authorizationHeader);
        String email = jwtService.extractUsername(token);
        try {
            return ResponseEntity.ok(adminService.unlockAccount(data, email));
        }catch (GlobalExceptionHandler ex) {
            Map<String, String> response = new HashMap<>();
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(response);
        }
    }

    @PostMapping("/deleteAccount")
    public ResponseEntity<?> deleteAccount(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, JustDataDto data){
        String token = extractToken(authorizationHeader);
        String email = jwtService.extractUsername(token);
        try {
            return ResponseEntity.ok(adminService.deleteAccount(data, email));
        }catch (GlobalExceptionHandler ex) {
            Map<String, String> response = new HashMap<>();
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(response);
        }
    }


    private String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Extracting the token after "Bearer "
        }else {
            return "Error validating your account! please login again or reload your browser if not. report issue@arunayurved.com";
        }

    }
}
