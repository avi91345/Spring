package com.springroot.free.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("avinabadas120720004@gmail.com"); // Sender email
        message.setTo(to);
        message.setSubject("Your OTP Code");
        message.setText("Dear User,\n\nYour OTP is: " + otp + "\n\nThis OTP is valid for 5 minutes only.\n\nThank you.");
        mailSender.send(message);
    }
}
