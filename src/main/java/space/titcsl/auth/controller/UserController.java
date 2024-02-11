package space.titcsl.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import space.titcsl.auth.entity.DeleteAccountRequest;
import space.titcsl.auth.entity.User;
import space.titcsl.auth.exception.GlobalExceptionHandler;
import space.titcsl.auth.service.AccountService;
import space.titcsl.auth.service.JwtService;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/${space.titcsl.auth.api.version}/user")
public class UserController {

    private final AccountService accountService;
    private final JwtService jwtService;

    @PostMapping("/deleteRequest")
    public ResponseEntity<?> deleteAccountRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        String token = extractToken(authorizationHeader);
        String email = jwtService.extractUsername(token);

        if (authorizationHeader != null){
            DeleteAccountRequest deleteAccountRequest = accountService.deleteAccountRequest(email);
            return ResponseEntity.ok("Your account delete request is successfully submitted you will get email of the process shortly. TITCSL.");
        }
        return (ResponseEntity<?>) ResponseEntity.status(401);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getAccountData(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        String token = extractToken(authorizationHeader);
        String email = jwtService.extractUsername(token);
        try {
            return ResponseEntity.ok(accountService.getAccountData(email));
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
            return "Error validating your account! please login again or reload your browser if not. report issues@titcsl.space";
        }

    }
}
