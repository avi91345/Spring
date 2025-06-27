package com.springroot.free.Service;


import com.springroot.free.Entity.User;
import com.springroot.free.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;

    @Autowired
    UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<Map<String, String>> verify(User loginRequest) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                ));
        if (authentication.isAuthenticated()) {
            String jwtToken = jwtService.generateToken(loginRequest.getUsername(), loginRequest.getEmail());
            String refreshToken = jwtService.generateRefreshToken(loginRequest.getUsername()); // Generate refresh token
            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", jwtToken);
            tokens.put("refreshToken", refreshToken);
            return ResponseEntity.ok(tokens);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public ResponseEntity<String> refresh(String refreshToken) {

            if (jwtService.validateRefreshToken(refreshToken)) {
                String username = jwtService.extractUsername(refreshToken, true);
                User user = userRepository.findByUsername(username);



                String newAccessToken = jwtService.generateToken(user.getUsername(), user.getEmail());
                return ResponseEntity.ok(newAccessToken);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
            }

    }


    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
