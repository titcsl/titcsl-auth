package space.titcsl.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import space.titcsl.auth.entity.User;
import space.titcsl.auth.exception.GlobalExceptionHandler;
import space.titcsl.auth.repository.UserRepository;
import space.titcsl.auth.service.EmailService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;

    public void sendVerificationEmail(String to, String otp) {
        Optional<User> optionalUser = userRepository.findByEmail(to);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (to != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject("Email Verification - TITCSL");
                message.setText(otp);
                javaMailSender.send(message);
            } else {
                throw new GlobalExceptionHandler("Error sending email. click on resend email.");
            }
        }
    }
    public void sendEmail(String to, String body, String subject){
        Optional<User> optionalUser = userRepository.findByEmail(to);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (to != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);
                javaMailSender.send(message);
            } else {
                throw new GlobalExceptionHandler("Error sending email. click on resend email.");
            }
        }
    }

}
