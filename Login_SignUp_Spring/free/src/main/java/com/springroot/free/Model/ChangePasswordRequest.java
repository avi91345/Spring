package com.springroot.free.Model;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String username;
    private String newPassword;
}
