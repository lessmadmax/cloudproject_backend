package com.cloudproject.community_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@Tag(name = "클라우드 프로젝트 체크", description = "CI/CD 테스트용 클라우드 프로젝트 체크 API")
@RestController
@RequestMapping("/api/cloud")
@RequiredArgsConstructor
public class HealthController {

    @GetMapping
    @Operation(summary = "클라우드 프로젝트 체크", description = "서버 상태를 확인합니다. CI/CD 테스트용입니다.")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now().toString(),
                "message", "Cloud Project is running"
        ));
    }
}

