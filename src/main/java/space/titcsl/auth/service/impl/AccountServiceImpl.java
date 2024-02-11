package space.titcsl.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import space.titcsl.auth.dto.RegisterRequest;
import space.titcsl.auth.entity.DeleteAccountRequest;
import space.titcsl.auth.entity.User;
import space.titcsl.auth.exception.GlobalExceptionHandler;
import space.titcsl.auth.repository.DeleteRequestRepository;
import space.titcsl.auth.repository.UserRepository;
import space.titcsl.auth.service.AccountService;
import space.titcsl.auth.service.EmailService;
import space.titcsl.auth.service.JwtService;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final DeleteRequestRepository deleteRequestRepository;

        public ResponseEntity<?> forgotPasswordRequest(String email){
        User user = userRepository.findByEmail(email).orElseThrow(()->new GlobalExceptionHandler("We cannot find the account that you're looking for, recheck the email and then try again. Thank You!"));
        String otp = UUID.randomUUID().toString().substring(0, 5);
        try {
            user.setHandleCode2(otp);
            userRepository.save(user);
            emailService.sendVerificationEmail(email, otp);
            return ResponseEntity.ok("Email containing otp is just sent to your account registered email address. Written by you. ");
        } catch (Exception e){
            throw new GlobalExceptionHandler("Error validating your account! sorry try after some time");
        }
        }

        public ResponseEntity<?> forgotPasswordValidation(String email, String otp, String password){
            User user = userRepository.findByEmail(email).orElseThrow(()->new GlobalExceptionHandler("We cannot find the account that you're looking for, recheck the email and then try again. Thank You!" + email));
            String preOtp = user.getHandleCode2();
            if (otp.equals(preOtp)){
                user.setPassword(passwordEncoder.encode(password));
                user.setHandleCode2("**TITCSL-PSS-CHANGED");
                userRepository.save(user);
                String subject = "Your Password change application has been successfully closed.";
                String body = "Dear, " + user.getFirstName() +
                        "\n" +
                        "We hope this email finds you well. We wanted to inform you that your password has been successfully reset for your account with us. Your security is our top priority, so we take password management seriously.\n" +
                        "\n" +
                        "If you did not initiate this password reset, please contact our support team immediately at support@titcsl.space.\n" +
                        "\n" +
                        "To ensure the security of your account, please refrain from sharing your password with anyone and consider using a unique and strong password that is not easily guessable.\n" +
                        "\n" +
                        "If you have any questions or concerns, please don't hesitate to reach out to us. We're here to help!\n" +
                        "\n" +
                        "Best regards,\n" +
                        "TITCSL" +
                        "\n" +
                        "\n" +
                        "*This is system generated email. Please do not reply to this email.";
                emailService.sendEmail(email, body, subject);
                return ResponseEntity.ok("Your have just set a new password to your account we are navigating you to login.");
            }else {
                throw new GlobalExceptionHandler("Invalid One-time-password. try checking it one more time.");
            }
        }

        public DeleteAccountRequest deleteAccountRequest(String email){
            User user = userRepository.findByEmail(email).orElseThrow(()->new GlobalExceptionHandler("We cannot find the account that you're looking for, recheck the email and then try again. Thank You!" + email));
            DeleteAccountRequest deleteAccountRequest = new DeleteAccountRequest();
            deleteAccountRequest.setUser_id(user.getUser_id());
            deleteAccountRequest.setStatus("CREATED");
            return deleteRequestRepository.save(deleteAccountRequest);
        }

        public ResponseEntity<?> getAccountData(String email){
            User user = userRepository.findByEmail(email).orElseThrow(()->new GlobalExceptionHandler("We cannot find the account that you're looking for, recheck the email and then try again. Thank You!" + email));
            return ResponseEntity.ok(user);
        }

    public ResponseEntity<?> updateAccount(RegisterRequest updateUserRequest, String token) {
        // Validate the token
        String userEmail = jwtService.extractUsername(token);
        User authUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GlobalExceptionHandler("You should not do this! please login again"));


        if (!authUser.getEmail().equals(updateUserRequest.getEmail()) && userRepository.existsByEmail(updateUserRequest.getEmail())) {
            throw new GlobalExceptionHandler("Email already exists! Please choose a different email.");
        }
        if (!authUser.getDisplayName().equals(updateUserRequest.getDisplayName()) && userRepository.existsByDisplayName(updateUserRequest.getDisplayName())) {
            throw new GlobalExceptionHandler("Display Name Is already given to someone");
        }

        User user = userRepository.findByEmail(updateUserRequest.getEmail())
                .orElseThrow(() -> new GlobalExceptionHandler("User not found with email: " + updateUserRequest.getEmail()));

        // Check if the authenticated user is updating their own profile
        if (!authUser.getEmail().equals(updateUserRequest.getEmail())) {
            throw new GlobalExceptionHandler("Invalid Request!");
        }


        // Update the user information based on the provided fields
        if (updateUserRequest.getFirstName() != null) {
            user.setFirstName(updateUserRequest.getFirstName());
        }

        if (updateUserRequest.getLastName() != null) {
            user.setLastName(updateUserRequest.getLastName());
        }


        if (updateUserRequest.getPassword() != null) {
            // You may want to handle password encoding here
            user.setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
        }

        if (updateUserRequest.getPhone() != null) {
            user.setPhone(updateUserRequest.getPhone());
        }

        if (updateUserRequest.getDisplayName() != null) {
            user.setDisplayName(updateUserRequest.getDisplayName());
        }

        userRepository.save(user);

        return ResponseEntity.ok("Account updated successfully!");
    }

}
