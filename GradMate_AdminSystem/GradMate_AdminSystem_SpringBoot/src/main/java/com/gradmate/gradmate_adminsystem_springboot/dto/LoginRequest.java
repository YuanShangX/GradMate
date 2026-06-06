package com.gradmate.gradmate_adminsystem_springboot.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
