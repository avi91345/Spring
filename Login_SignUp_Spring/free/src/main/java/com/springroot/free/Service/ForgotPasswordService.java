package com.springroot.free.Service;

import com.springroot.free.Entity.ForgotPasswordOtp;
import com.springroot.free.Entity.User;
import com.springroot.free.Repository.ForgotPasswordOtpRepository;
import com.springroot.free.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class ForgotPasswordService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ForgotPasswordOtpRepository otpRepository;

    @Autowired
    private EmailService emailService; // Service to send emails

    public String initiateForgotPassword(String username) {
        // Check if user exists

        User userdata = userRepository.findByUsername(username);
        if (userdata==null) {
            return "User not found";
        }


        String email = userdata.getEmail();

        // Generate OTP
        String otp = generateOtp();
        String encodedotp=passwordEncoder.encode(otp);

        // Set expiration time for OTP (5 minutes from now)
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        // Check if OTP already exists for this user
        ForgotPasswordOtp otpEntity = otpRepository.findByUsername(username);
        if(otpEntity==null){
            otpEntity=new ForgotPasswordOtp();
            otpEntity.setUsername(username);
        }


        // Update OTP and expiration time
        otpEntity.setOtp(encodedotp);
        otpEntity.setExpirationTime(expirationTime);
        otpEntity.setUsed(false);
        otpRepository.save(otpEntity);

        // Send OTP to user's email
        emailService.sendOtpEmail(email, otp);

        return "OTP sent to " + email;
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate 6-digit OTP
        return String.valueOf(otp);
    }


    public boolean verifyOtp(String username, String otp) {
        ForgotPasswordOtp otpverify= otpRepository.findByUsername(username);
        if (otpverify == null || otpverify.isUsed()) {
            return false; // OTP not found or already used

        }

        if (passwordEncoder.matches(otp, otpverify.getOtp()) && otpverify.getExpirationTime().isAfter(LocalDateTime.now())) {
            otpverify.setUsed(true);
            otpRepository.save(otpverify);
            return true;
        }

        return false;

    }

    public String changePassword(String username, String newPassword) {
        ForgotPasswordOtp otpcheck = otpRepository.findByUsername(username);
        if (otpcheck == null || !otpcheck.isUsed()) {
            return "OTP not verified.";
        }
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "User not found.";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "Password changed successfully.";
    }

}
