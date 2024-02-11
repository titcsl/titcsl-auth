package space.titcsl.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.titcsl.auth.dto.*;
import space.titcsl.auth.entity.User;
import space.titcsl.auth.exception.GlobalExceptionHandler;
import space.titcsl.auth.repository.UserRepository;
import space.titcsl.auth.service.AuthenticationService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/${space.titcsl.auth.api.version}/authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest register) {
        try {
            ResponseEntity<User> user = (ResponseEntity<User>) authenticationService.register(register);
            return ResponseEntity.ok(user);
        } catch (GlobalExceptionHandler ex) {
            Map<String, String> response = new HashMap<>();
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(response);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest login) {
        try {
            JwtAuthenticationResponse tokens = authenticationService.login(login);
            return ResponseEntity.ok(tokens);
        } catch (GlobalExceptionHandler ex) {
            Map<String, String> response = new HashMap<>();
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(response);
        }
    }

    @PostMapping("/refresh/token")
    public ResponseEntity<JwtAuthenticationResponse> refreshAndGetToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyRequest verifyRequest){
        String email = verifyRequest.getEmail();
        String otp = verifyRequest.getOtp();
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new GlobalExceptionHandler("The email you have entered is not found in our database. this is not complicated the email you have entered doesn't exist."));
            if (!user.isVerified() && otp.equals(user.getHandleCode1())){
                user.setVerified(true);
                user.setHandleCode1("**TITCSL-VERIFIED");
                userRepository.save(user);
                return ResponseEntity.ok("Your account is verified successfully! now we are navigating you to login.");
            }else {
                return ResponseEntity.ok("The otp we have got is incorrect! please try again with correct otp.");
            }
        } catch (GlobalExceptionHandler ex) {
            Map<String, String> response = new HashMap<>();
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(response);
        }
    }



}
