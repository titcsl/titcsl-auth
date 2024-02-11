package space.titcsl.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import space.titcsl.auth.dto.JwtAuthenticationResponse;
import space.titcsl.auth.dto.LoginRequest;
import space.titcsl.auth.dto.RefreshTokenRequest;
import space.titcsl.auth.dto.RegisterRequest;
import space.titcsl.auth.entity.Role;
import space.titcsl.auth.entity.User;
import space.titcsl.auth.exception.GlobalExceptionHandler;
import space.titcsl.auth.repository.UserRepository;
import space.titcsl.auth.service.AuthenticationService;
import space.titcsl.auth.service.EmailService;
import space.titcsl.auth.service.JwtService;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> register(RegisterRequest register){

        if (userRepository.existsByDisplayName(register.getDisplayName())) {
            throw new GlobalExceptionHandler("Display name already exists! Try using another cool display name for you.");
        }

        if (userRepository.existsByEmail(register.getEmail())) {
            throw new GlobalExceptionHandler("Email already exists! login with email that you've entered or try resetting password of that email.");
        }

        if (userRepository.existsByPhone(register.getPhone())) {
            throw new GlobalExceptionHandler("The phone number already exists! if it is your phone number then you've created your account already. please check your account on find my account portal - TITCSL then try to reset it.");
        }

        String otp = UUID.randomUUID().toString().substring(0, 5);

        User user = new User();
        user.setEmail(register.getEmail());
        user.setDisplayName(register.getDisplayName());
        user.setVerified(false);
        user.setHandleCode1(otp);
        user.setFirstName(register.getFirstName());
        user.setLastName(register.getLastName());
        user.setPhone(register.getPhone());
        user.setRole(Role.DEFAULT);
        user.setLocked(false);
        user.setPassword(passwordEncoder.encode(register.getPassword()));

        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), otp);

        return ResponseEntity.ok("Your account is on hold! please verify it. if not it will get auto-deleted in 14 days.");
    }

    public JwtAuthenticationResponse login(LoginRequest login) {
        User user = userRepository.findByEmail(login.getEmail()).orElseThrow(() -> new GlobalExceptionHandler("The email you have entered is not found in our database. this is complicated, but in short the email doesn't exist."));
        String otp = UUID.randomUUID().toString().substring(0, 5);

        if (!user.isLocked()) {

            if (user.isVerified()) {
                try {
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));
                    var access_token = jwtService.generateToken(user);
                    var refresh_token = jwtService.generateRefreshToken(new HashMap<>(), user);

                    JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
                    jwtAuthenticationResponse.setAccess_token(access_token);
                    jwtAuthenticationResponse.setRefresh_token(refresh_token);
                    return jwtAuthenticationResponse;
                } catch (BadCredentialsException e) {
                    throw new GlobalExceptionHandler("Invalid password we could not verify the password that is present in database. Please try again.");
                }
            } else {
                emailService.sendVerificationEmail(login.getEmail(), otp);
                user.setHandleCode1(otp);
                userRepository.save(user);
                throw new GlobalExceptionHandler("Your account is not verified till date. we have sent otp to you email just now! go to verifying portal and verify it. else you data will be deleted in 7 days after creation.");
            }
        }else {
            throw new GlobalExceptionHandler("You account is locked. for some major security reason please file the application for unlocking your account in the. Unlock Account Portal - TITCSL.");
        }

    }

    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest){
        String userEmail =  jwtService.extractUsername(refreshTokenRequest.getRefresh_token());
        User user = userRepository.findByEmail(userEmail).orElseThrow(()->new GlobalExceptionHandler("Error validating this exception. Please login again."));

        if (jwtService.isTokenValid(refreshTokenRequest.getRefresh_token(), user)) {
            var jwt = jwtService.generateToken(user);
            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
            jwtAuthenticationResponse.setAccess_token(jwt);
            jwtAuthenticationResponse.setRefresh_token(refreshTokenRequest.getRefresh_token());
            return jwtAuthenticationResponse;
        } else {
            throw  new GlobalExceptionHandler("Please login again. for securing your account more than others.");
        }
    }



}
