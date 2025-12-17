package com.cloudproject.community_backend.controller;

import com.cloudproject.community_backend.dto.DashboardMetricsDTO;
import com.cloudproject.community_backend.entity.User;
import com.cloudproject.community_backend.entity.UserRole;
import com.cloudproject.community_backend.repository.UserRepository;
import com.cloudproject.community_backend.security.JwtUtil;
import com.cloudproject.community_backend.service.AdminMetricService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminMetricController {

    private final AdminMetricService metricService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @GetMapping("/metrics")
    public ResponseEntity<DashboardMetricsDTO> getDashboardMetrics(HttpServletRequest request) {
        verifyAdmin(request);
        return ResponseEntity.ok(metricService.getCurrentMetrics());
    }

    /**
     * 기존 SecurityConfig를 수정하지 않기 위해, 이 컨트롤러 내부에서 ADMIN 권한을 검증합니다.
     */
    private void verifyAdmin(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 토큰이 없습니다");
        }
        String token = bearerToken.substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다"));
        if (user.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자 권한이 필요합니다");
        }
    }
}


