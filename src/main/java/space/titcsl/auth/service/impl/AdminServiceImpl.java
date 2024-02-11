package space.titcsl.auth.service.impl;

import jakarta.persistence.NonUniqueResultException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import space.titcsl.auth.dto.JustDataDto;
import space.titcsl.auth.entity.DeleteAccountRequest;
import space.titcsl.auth.entity.LockAccountRequest;
import space.titcsl.auth.entity.User;
import space.titcsl.auth.exception.GlobalExceptionHandler;
import space.titcsl.auth.repository.DeleteRequestRepository;
import space.titcsl.auth.repository.LockAccountRepository;
import space.titcsl.auth.repository.UserRepository;
import space.titcsl.auth.service.AdminService;
import space.titcsl.auth.service.EmailService;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {

    private final LockAccountRepository lockAccountRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final DeleteRequestRepository deleteRequestRepository;

    public ResponseEntity<?> deleteAccount(JustDataDto data, String email){
        String clientEmail = data.getEmail();
        User user = userRepository.findByEmail(clientEmail).orElseThrow(()-> new GlobalExceptionHandler("The email you have entered is not found in our database. this is not complicated the email you have entered doesn't exist."));
        DeleteAccountRequest deleteAccountRequest = deleteRequestRepository.findByEmail(clientEmail).orElseThrow(()-> new GlobalExceptionHandler("The email you have entered is not found in our database. this is not complicated the email you have entered doesn't exist."));
        try {
            userRepository.deleteById(user.getUser_id());
            deleteAccountRequest.setDeleted_by(email);
            deleteRequestRepository.save(deleteAccountRequest);
            emailService.sendEmail(clientEmail, "accout deleted succesfully", "ok");
        }catch (GlobalExceptionHandler | NonUniqueResultException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(response);
        }
        return null;
    }


    public ResponseEntity<?> lockAccount(JustDataDto data, String email){
        String clientEmail = data.getEmail();
        String message = data.getMessage();
        LockAccountRequest lockAccountRequest = new LockAccountRequest();
        User user = userRepository.findByEmail(clientEmail).orElseThrow(()-> new GlobalExceptionHandler("The email you have entered is not found in our database. this is not complicated the email you have entered doesn't exist."));
        try {
            user.setLocked(true);
            lockAccountRequest.setRequest_id(user.getUser_id());
            lockAccountRequest.setLocked_by(email);
            lockAccountRequest.setEmail(clientEmail);
            lockAccountRequest.setWhy(message);
            lockAccountRequest.setUnlock_by("**TITCSL-NON-UNLOCKED-NOT.");
            lockAccountRepository.save(lockAccountRequest);
            try{
                LockAccountRequest lockAccountRequest1 = lockAccountRepository.findByEmail(clientEmail).orElseThrow(()-> new GlobalExceptionHandler("The email you have entered is not found in our database. this is not complicated the email you have entered doesn't exist."));
                String body = "Your account is locked for Lock request ref id + " + lockAccountRequest.getLock_id() + "for this matter" + lockAccountRequest1.getWhy();
                lockAccountRepository.save(lockAccountRequest);
                emailService.sendEmail(clientEmail, body, "ok");
                return ResponseEntity.ok("The given account is locked successfully!.");

            }catch (GlobalExceptionHandler | NonUniqueResultException ex) {
                Map<String, String> response = new HashMap<>();
                response.put("message", ex.getMessage());
                return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(response);
            }
        }catch (GlobalExceptionHandler ex) {
            Map<String, String> response = new HashMap<>();
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(response);
        }
    }

    public ResponseEntity<?> unlockAccount(JustDataDto data, String email){
        String lock_id = data.getLock_id();
        LockAccountRequest lockAccountRequest = lockAccountRepository.findById(lock_id).orElseThrow(()-> new GlobalExceptionHandler("The email you have entered is not found in our database. this is not complicated the email you have entered doesn't exist."));
        User user = userRepository.findById(lockAccountRequest.getRequest_id()).orElseThrow(()-> new GlobalExceptionHandler("The email you have entered is not found in our database. this is not complicated the email you have entered doesn't exist."));
        try {
            user.setLocked(false);
            userRepository.save(user);
            String body = "Your account is unlocked for unLock request ref id + " + lockAccountRequest.getLock_id();
            lockAccountRequest.setUnlock_by(email);
            lockAccountRepository.save(lockAccountRequest);
            emailService.sendEmail(user.getEmail(), body, "ok");
            return ResponseEntity.ok("The given account is unlocked successfully!.");
        }catch (GlobalExceptionHandler ex) {
            Map<String, String> response = new HashMap<>();
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(response);
        }
    }

}
