package com.springroot.free.Repository;



import com.springroot.free.Entity.ForgotPasswordOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgotPasswordOtpRepository extends JpaRepository<ForgotPasswordOtp, Long> {

    // Find OTP record by username
    ForgotPasswordOtp findByUsername(String username);
}

