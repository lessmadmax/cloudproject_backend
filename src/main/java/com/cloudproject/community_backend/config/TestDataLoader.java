package com.cloudproject.community_backend.config;

import com.cloudproject.community_backend.entity.*;
import com.cloudproject.community_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class TestDataLoader {

    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Profile("!prod")
    public CommandLineRunner loadTestData() {
        return args -> {
            School testSchool = new School();
            testSchool.setName("서울중학교");
            schoolRepository.save(testSchool);

            User student1 = new User();
            student1.setEmail("student1@test.com");
            student1.setPassword(passwordEncoder.encode("password123"));
            student1.setUsername("학생1");
            student1.setSchool(testSchool);
            student1.setGrade(1);
            student1.setRole(UserRole.STUDENT);
            student1.setIsSeniorVerified(false);
            userRepository.save(student1);

            User senior1 = new User();
            senior1.setEmail("senior1@test.com");
            senior1.setPassword(passwordEncoder.encode("password123"));
            senior1.setUsername("선배1");
            senior1.setSchool(testSchool);
            senior1.setGrade(2);
            senior1.setRole(UserRole.STUDENT);
            senior1.setIsSeniorVerified(true);
            senior1.setSeniorVerifiedAt(LocalDateTime.now().minusDays(7));
            userRepository.save(senior1);

            User senior2 = new User();
            senior2.setEmail("senior2@test.com");
            senior2.setPassword(passwordEncoder.encode("password123"));
            senior2.setUsername("선배2");
            senior2.setSchool(testSchool);
            senior2.setGrade(3);
            senior2.setRole(UserRole.STUDENT);
            senior2.setIsSeniorVerified(true);
            senior2.setSeniorVerifiedAt(LocalDateTime.now().minusDays(30));
            userRepository.save(senior2);

            User admin = new User();
            admin.setEmail("admin@test.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setUsername("관리자");
            admin.setSchool(testSchool);
            admin.setGrade(null);
            admin.setRole(UserRole.ADMIN);
            admin.setIsSeniorVerified(false);
            userRepository.save(admin);

            Post normalPost = new Post();
            normalPost.setTitle("안녕하세요");
            normalPost.setContent("첫 게시글입니다");
            normalPost.setAuthor(student1);
            normalPost.setBoardType(PostBoardType.TALK);
            normalPost.setBad(false);
            postRepository.save(normalPost);

            Post questionPost = new Post();
            questionPost.setTitle("수학 질문있어요");
            questionPost.setContent("이차방정식 풀이 방법을 모르겠어요");
            questionPost.setAuthor(student1);
            questionPost.setBoardType(PostBoardType.QUESTION);
            questionPost.setBad(false);
            postRepository.save(questionPost);

            Post badPost = new Post();
            badPost.setTitle("테스트");
            badPost.setContent("야이 멍청아");
            badPost.setAuthor(student1);
            badPost.setBoardType(PostBoardType.TALK);
            badPost.setBad(true);
            postRepository.save(badPost);
        };
    }
}
