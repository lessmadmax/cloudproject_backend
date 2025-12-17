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
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Profile("!prod")
    public CommandLineRunner loadTestData() {
        return args -> {
            // 이미 데이터가 있으면 초기화 건너뛰기
            if (userRepository.count() > 0) {
                System.out.println("✅ 테스트 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
                return;
            }

            System.out.println("📝 테스트 데이터 초기화 시작...");

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

            User student2 = new User();
            student2.setEmail("student2@test.com");
            student2.setPassword(passwordEncoder.encode("password123"));
            student2.setUsername("학생2");
            student2.setSchool(testSchool);
            student2.setGrade(1);
            student2.setRole(UserRole.STUDENT);
            student2.setIsSeniorVerified(false);
            userRepository.save(student2);

            User student3 = new User();
            student3.setEmail("student3@test.com");
            student3.setPassword(passwordEncoder.encode("password123"));
            student3.setUsername("학생3");
            student3.setSchool(testSchool);
            student3.setGrade(2);
            student3.setRole(UserRole.STUDENT);
            student3.setIsSeniorVerified(false);
            userRepository.save(student3);

            User student4 = new User();
            student4.setEmail("student4@test.com");
            student4.setPassword(passwordEncoder.encode("password123"));
            student4.setUsername("학생4");
            student4.setSchool(testSchool);
            student4.setGrade(2);
            student4.setRole(UserRole.STUDENT);
            student4.setIsSeniorVerified(false);
            userRepository.save(student4);

            User student5 = new User();
            student5.setEmail("student5@test.com");
            student5.setPassword(passwordEncoder.encode("password123"));
            student5.setUsername("학생5");
            student5.setSchool(testSchool);
            student5.setGrade(3);
            student5.setRole(UserRole.STUDENT);
            student5.setIsSeniorVerified(false);
            userRepository.save(student5);

            User student6 = new User();
            student6.setEmail("student6@test.com");
            student6.setPassword(passwordEncoder.encode("password123"));
            student6.setUsername("학생6");
            student6.setSchool(testSchool);
            student6.setGrade(3);
            student6.setRole(UserRole.STUDENT);
            student6.setIsSeniorVerified(false);
            userRepository.save(student6);

            User student7 = new User();
            student7.setEmail("student7@test.com");
            student7.setPassword(passwordEncoder.encode("password123"));
            student7.setUsername("학생7");
            student7.setSchool(testSchool);
            student7.setGrade(1);
            student7.setRole(UserRole.STUDENT);
            student7.setIsSeniorVerified(false);
            userRepository.save(student7);

            User student8 = new User();
            student8.setEmail("student8@test.com");
            student8.setPassword(passwordEncoder.encode("password123"));
            student8.setUsername("학생8");
            student8.setSchool(testSchool);
            student8.setGrade(2);
            student8.setRole(UserRole.STUDENT);
            student8.setIsSeniorVerified(false);
            userRepository.save(student8);

            User student9 = new User();
            student9.setEmail("student9@test.com");
            student9.setPassword(passwordEncoder.encode("password123"));
            student9.setUsername("학생9");
            student9.setSchool(testSchool);
            student9.setGrade(3);
            student9.setRole(UserRole.STUDENT);
            student9.setIsSeniorVerified(false);
            userRepository.save(student9);

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

            // ========== 최근 7일 내 metrics 테스트용 데이터 ==========
            LocalDateTime now = LocalDateTime.now();

            // 질문 1: 답변 있음 (1일 전) - 선배가 답변
            Post q1 = new Post();
            q1.setTitle("질문 1: 선배 답변 있음");
            q1.setContent("이 질문에는 선배가 답변을 달았어요");
            q1.setAuthor(student2);
            q1.setBoardType(PostBoardType.QUESTION);
            q1.setBad(false);
            q1.setCreatedAt(now.minusDays(1));
            Post savedQ1 = postRepository.save(q1);

            Comment answer1 = new Comment();
            answer1.setContent("이 질문에 대한 선배의 답변입니다");
            answer1.setAuthor(senior1);
            answer1.setPost(savedQ1);
            answer1.setCreatedAt(now.minusDays(1).plusHours(2)); // 질문 후 2시간 뒤 답변
            answer1.setBad(false);
            answer1.setAuthorName(senior1.getUsername());
            commentRepository.save(answer1);

            // 질문 2: 답변 있음 (3일 전) - 선배가 답변
            Post q2 = new Post();
            q2.setTitle("질문 2: 선배 답변 있음");
            q2.setContent("이 질문에도 선배가 답변을 달았어요");
            q2.setAuthor(student3);
            q2.setBoardType(PostBoardType.QUESTION);
            q2.setBad(false);
            q2.setCreatedAt(now.minusDays(3));
            Post savedQ2 = postRepository.save(q2);

            Comment answer2 = new Comment();
            answer2.setContent("선배의 답변입니다");
            answer2.setAuthor(senior2);
            answer2.setPost(savedQ2);
            answer2.setCreatedAt(now.minusDays(3).plusHours(1)); // 질문 후 1시간 뒤 답변
            answer2.setBad(false);
            answer2.setAuthorName(senior2.getUsername());
            commentRepository.save(answer2);

            // 질문 3: 답변 있음 (5일 전) - 학생이 답변 (요구사항: 학생도 포함)
            Post q3 = new Post();
            q3.setTitle("질문 3: 학생 답변 있음");
            q3.setContent("이 질문에는 학생이 답변을 달았어요");
            q3.setAuthor(student4);
            q3.setBoardType(PostBoardType.QUESTION);
            q3.setBad(false);
            q3.setCreatedAt(now.minusDays(5));
            Post savedQ3 = postRepository.save(q3);

            Comment answer3 = new Comment();
            answer3.setContent("학생의 답변입니다");
            answer3.setAuthor(student5);
            answer3.setPost(savedQ3);
            answer3.setCreatedAt(now.minusDays(5).plusHours(3)); // 질문 후 3시간 뒤 답변
            answer3.setBad(false);
            answer3.setAuthorName(student5.getUsername());
            commentRepository.save(answer3);

            // 질문 4: 답변 없음 (2일 전) - 미답변 백로그
            Post q4 = new Post();
            q4.setTitle("질문 4: 답변 없음");
            q4.setContent("이 질문은 아직 답변이 없어요");
            q4.setAuthor(student6);
            q4.setBoardType(PostBoardType.QUESTION);
            q4.setBad(false);
            q4.setCreatedAt(now.minusDays(2));
            postRepository.save(q4);

            // 질문 5: 답변 없음 (4일 전) - 미답변 백로그
            Post q5 = new Post();
            q5.setTitle("질문 5: 답변 없음");
            q5.setContent("이 질문도 아직 답변이 없어요");
            q5.setAuthor(student7);
            q5.setBoardType(PostBoardType.QUESTION);
            q5.setBad(false);
            q5.setCreatedAt(now.minusDays(4));
            postRepository.save(q5);

            // 최근 7일 내 일반 게시글/댓글 (불량률 계산용)
            Post recentPost1 = new Post();
            recentPost1.setTitle("최근 게시글 1");
            recentPost1.setContent("최근 7일 내 정상 게시글");
            recentPost1.setAuthor(student8);
            recentPost1.setBoardType(PostBoardType.TALK);
            recentPost1.setBad(false);
            recentPost1.setCreatedAt(now.minusDays(1));
            Post savedRecentPost1 = postRepository.save(recentPost1);

            Comment recentComment1 = new Comment();
            recentComment1.setContent("최근 정상 댓글");
            recentComment1.setAuthor(student9);
            recentComment1.setPost(savedRecentPost1);
            recentComment1.setCreatedAt(now.minusDays(1));
            recentComment1.setBad(false);
            recentComment1.setAuthorName(student9.getUsername());
            commentRepository.save(recentComment1);

            Post recentPost2 = new Post();
            recentPost2.setTitle("최근 게시글 2");
            recentPost2.setContent("최근 7일 내 정상 게시글 2");
            recentPost2.setAuthor(student1);
            recentPost2.setBoardType(PostBoardType.TALK);
            recentPost2.setBad(false);
            recentPost2.setCreatedAt(now.minusDays(2));
            postRepository.save(recentPost2);

            // 최근 7일 내 불량 게시글/댓글 (불량률 계산용)
            Post badRecentPost = new Post();
            badRecentPost.setTitle("불량 게시글");
            badRecentPost.setContent("욕설 포함 게시글");
            badRecentPost.setAuthor(student2);
            badRecentPost.setBoardType(PostBoardType.TALK);
            badRecentPost.setBad(true);
            badRecentPost.setCreatedAt(now.minusDays(2));
            Post savedBadPost = postRepository.save(badRecentPost);

            Comment badRecentComment = new Comment();
            badRecentComment.setContent("불량 댓글");
            badRecentComment.setAuthor(student3);
            badRecentComment.setPost(savedRecentPost1);
            badRecentComment.setCreatedAt(now.minusDays(1));
            badRecentComment.setBad(true);
            badRecentComment.setAuthorName(student3.getUsername());
            commentRepository.save(badRecentComment);

            // 최근 7일 내 신고 (검토 시간 계산용)
            Report report1 = Report.builder()
                .reporter(student1)
                .targetType(TargetType.POST)
                .targetId(savedBadPost.getId())
                .reason(ReportReason.PROFANITY)
                .status(ReportStatus.APPROVED)
                .createdAt(now.minusDays(1))
                .reviewedAt(now.minusDays(1).plusHours(3)) // 3시간 후 검토 완료
                .reviewer(admin)
                .reviewNote("욕설 사용으로 승인")
                .build();
            reportRepository.save(report1);

            Report report2 = Report.builder()
                .reporter(student2)
                .targetType(TargetType.COMMENT)
                .targetId(badRecentComment.getId())
                .reason(ReportReason.BULLYING)
                .status(ReportStatus.APPROVED)
                .createdAt(now.minusDays(2))
                .reviewedAt(now.minusDays(2).plusHours(5)) // 5시간 후 검토 완료
                .reviewer(admin)
                .reviewNote("괴롭힘으로 승인")
                .build();
            reportRepository.save(report2);

            Report report3 = Report.builder()
                .reporter(student3)
                .targetType(TargetType.POST)
                .targetId(savedRecentPost1.getId())
                .reason(ReportReason.SPAM)
                .status(ReportStatus.REJECTED)
                .createdAt(now.minusDays(3))
                .reviewedAt(now.minusDays(3).plusHours(2)) // 2시간 후 반려
                .reviewer(admin)
                .reviewNote("스팸 아님으로 반려")
                .build();
            reportRepository.save(report3);

            System.out.println("✅ 최근 7일 metrics 테스트 데이터 추가 완료");
        };
    }
}
