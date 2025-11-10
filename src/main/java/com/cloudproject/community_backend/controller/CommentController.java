package com.cloudproject.community_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.cloudproject.community_backend.entity.Comment;
import com.cloudproject.community_backend.entity.Post;
import com.cloudproject.community_backend.entity.User;
import com.cloudproject.community_backend.repository.PostRepository;
import com.cloudproject.community_backend.repository.UserRepository;
import com.cloudproject.community_backend.service.CommentService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Tag(name = "댓글", description = "댓글 관련 API")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // DTO 정의 (Swagger에서 Request Body를 명확히 보여주기)
    public record CommentCreateRequest(
            @Schema(description = "댓글 내용", example = "좋은 글이네요!")
            String content,

            @Schema(description = "작성자 ID", example = "1")
            Long authorId,

            @Schema(description = "게시물 ID", example = "1")
            Long postId
    
    ) {}

    @Operation(summary = "댓글 작성", description = "새로운 댓글을 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 작성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public Comment createComment(@RequestBody CommentCreateRequest req) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        String authenticatedEmail = authentication.getName();

        User author = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("작성자 없음"));
        Post post = postRepository.findById(req.postId())
                .orElseThrow(() -> new RuntimeException("게시물 없음"));

        Comment comment = new Comment();
        comment.setContent(req.content());
        comment.setAuthor(author);
        comment.setPost(post);
        comment.setAuthorName(author.getUsername());

        return commentService.createComment(comment); 
    }

    @Operation(summary = "댓글 전체 조회", description = "등록된 모든 댓글을 조회합니다.")
    @GetMapping
    public List<Comment> getAllComments() {
        return (List<Comment>) commentService.getAllComments();
    }
    @Operation(summary = "악플 댓글 조회", description = "AI가 판별한 악플만 조회합니다.")
    @GetMapping("/bad")
    public List<Comment> getBadComments() {
        return commentService.getBadComments();
}
}
