package com.cloudproject.community_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.cloudproject.community_backend.entity.School;
import com.cloudproject.community_backend.entity.User;
import com.cloudproject.community_backend.repository.SchoolRepository;
import com.cloudproject.community_backend.repository.UserRepository;
import com.cloudproject.community_backend.service.OcrSpaceOcrService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;



@Tag(name = "회원", description = "회원 관련 API (회원가입/조회)")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SchoolRepository schoolRepository;
    private final OcrSpaceOcrService ocrSpaceOcrService;
    
    @PostMapping(
        value = "/register",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE // ✅ Swagger에서 파일 업로드 가능
    )
    @Operation(summary = "회원가입 (학생증 인증)", description = "학생증 인증을 통해 새로운 사용자를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "인증 실패 / 중복 이메일")
    })
    public ResponseEntity<?> register(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String username,
            @RequestParam String schoolName,
            @RequestPart MultipartFile studentCard
    ) throws IOException {

        // ✅ 이메일 중복 체크
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 가입된 이메일입니다.");
        }

        // 1️⃣ 학생증 OCR → 학교명 추출
        String recognizedSchool = ocrSpaceOcrService.extractSchoolName(studentCard);
        System.out.println("🎓 OCR 반환 학교명: " + recognizedSchool);

        // 공백 제거 + 대소문자 무시
        String normalizedInput = schoolName.replaceAll("\\s+", "").toLowerCase();
        String normalizedOcr = recognizedSchool.replaceAll("\\s+", "").toLowerCase();

        if (!normalizedInput.equals(normalizedOcr)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("학교 인증 실패. 입력한 학교명: " + schoolName + ", OCR 결과: " + recognizedSchool);
        }

        System.out.println("OCR 반환 학교명: " + recognizedSchool);

        // 2️⃣ DB에서 학교 조회 (없으면 생성)
        School school = schoolRepository.findByName(schoolName)
                .orElseGet(() -> schoolRepository.save(new School(null, schoolName, null)));


        // ✅ 가입 진행
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setUsername(username);
        user.setSchool(school);
        return ResponseEntity.ok(userRepository.save(user));
    }

    @Operation(summary = "모든 사용자 조회", description = "등록된 모든 사용자를 조회합니다.")
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Operation(summary = "특정 사용자 조회", description = "ID로 특정 사용자를 조회합니다.")
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
