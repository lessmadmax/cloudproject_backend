
package com.cloudproject.community_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Long userId;
    private String username;
    private String email;
    private boolean isSeniorVerified;
    private String role;  // STUDENT, ADMIN
}
