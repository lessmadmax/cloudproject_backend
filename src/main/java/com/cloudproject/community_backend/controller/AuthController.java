
package com.cloudproject.community_backend.controller;

import com.cloudproject.community_backend.dto.LoginRequest;
import com.cloudproject.community_backend.dto.LoginResponse;
import com.cloudproject.community_backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "로그인/토큰 발급")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
        summary = "로그인",
        description = "email, password로 로그인하여 JWT 토큰을 발급받습니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{ \"email\": \"test@example.com\", \"password\": \"password123\" }")
            )
        )
    )
    @ApiResponse(
        responseCode = "200",
        description = "로그인 성공",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = "{ \"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\" }")
        )
    )
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
