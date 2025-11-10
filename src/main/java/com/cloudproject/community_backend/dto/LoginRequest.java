
package com.cloudproject.community_backend.dto;


public record LoginRequest(
    String email,
    String password
) {}
