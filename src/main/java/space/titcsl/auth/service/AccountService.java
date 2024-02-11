package space.titcsl.auth.service;

import org.springframework.http.ResponseEntity;
import space.titcsl.auth.entity.DeleteAccountRequest;

public interface AccountService {
    ResponseEntity<?> forgotPasswordRequest(String email);
    ResponseEntity<?> forgotPasswordValidation(String email, String otp, String password);

    ResponseEntity<?> getAccountData(String email);
    DeleteAccountRequest deleteAccountRequest(String email);
}
