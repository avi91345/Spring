package com.springroot.free.Controller;

import com.springroot.free.Entity.User;
import com.springroot.free.Model.ChangePasswordRequest;
import com.springroot.free.Model.UserName;
import com.springroot.free.Model.VerifyOtpRequest;
import com.springroot.free.Repository.UserRepository;
import com.springroot.free.Service.ForgotPasswordService;
import com.springroot.free.Service.UserService;
import jakarta.persistence.GeneratedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController {


    private final UserService userService;
    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @Autowired
    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User loginRequest) {
        return userService.verify(loginRequest);
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<String> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        return userService.refresh(refreshToken);
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<?> forgotPassword(@RequestBody UserName username) {

        String response = forgotPasswordService.initiateForgotPassword(username.username);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify_otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
        boolean isVerified = forgotPasswordService.verifyOtp(request.getUsername(), request.getOtp());

        if (isVerified) {
            return ResponseEntity.ok("OTP verified successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired OTP.");
        }
    }


    @PostMapping("/change_password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        String response = forgotPasswordService.changePassword(request.getUsername(), request.getNewPassword());
        return response.equals("Password changed successfully.")
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }






}
