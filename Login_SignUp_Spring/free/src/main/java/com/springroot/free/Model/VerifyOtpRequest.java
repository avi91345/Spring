package com.springroot.free.Model;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String username;
    private String otp;
}
