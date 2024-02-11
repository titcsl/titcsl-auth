package space.titcsl.auth.service;

public interface EmailService {
    void sendVerificationEmail(String to, String otp);
    void sendEmail(String to, String body, String subject);
}
