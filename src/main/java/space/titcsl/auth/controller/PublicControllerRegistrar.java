package space.titcsl.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import space.titcsl.auth.dto.JustDataDto;
import space.titcsl.auth.dto.VerifyRequest;
import space.titcsl.auth.entity.User;
import space.titcsl.auth.exception.GlobalExceptionHandler;
import space.titcsl.auth.repository.UserRepository;
import space.titcsl.auth.service.AccountService;
import space.titcsl.auth.service.JwtService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/${space.titcsl.auth.api.version}/publicRegistrar")
public class PublicControllerRegistrar {

    public final UserRepository userRepository;
    public final JwtService jwtService;
    public final AccountService accountService;


    @PostMapping("/forgotPasswordRequest")
    public ResponseEntity<?> forgotPasswordRequest(@RequestBody JustDataDto data){
        String email = data.getEmail();

        try {
            return accountService.forgotPasswordRequest(email);
        } catch (GlobalExceptionHandler ex){
            Map<String, String> response = new HashMap<>();
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(401)).body(response);
        }
    }

    @PostMapping("/forgotPasswordValidation")
    public ResponseEntity<?> forgotPasswordValidation(@RequestBody JustDataDto data){

        String email = data.getEmail();
        String otp = data.getOtp();
        String password = data.getPassword();
        try {
            return accountService.forgotPasswordValidation(email, otp, password);
        }catch (GlobalExceptionHandler ex){
            Map<String, String> response = new HashMap<>();
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(response);
        }
    }

    @PostMapping("/isLocked")
    public boolean isLocked(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        String token = extractToken(authorizationHeader);
        String email = jwtService.extractUsername(token);

        if (authorizationHeader != null){
            User user = userRepository.findByEmail(email).orElseThrow(()->new GlobalExceptionHandler("You account is locked. for some major security reason please file the application for unlocking your account in the. Unlock Account Portal - TITCSL."));
            return user.isLocked();
        }
        return true;
    }


    @PostMapping("/findAccount")
    public boolean checkAccountAvailability(@RequestBody Map<String, String> data) {
        String phone = data.get("phone");
        String email = data.get("email");
        String displayName = data.get("displayName");

        if (!(phone == null)){
            return userRepository.existsByPhone(phone);
        }if (!(email == null)){
            return userRepository.existsByEmail(email);
        } if (!(displayName == null)){
            return userRepository.existsByDisplayName(displayName);
        }
        return false;
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Extracting the token after "Bearer "
        }else {
            return "Error validating your account! please login again or reload your browser if not. report issue@arunayurved.com";
        }

    }

    
}
