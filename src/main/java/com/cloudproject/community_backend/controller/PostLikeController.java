package com.cloudproject.community_backend.controller;

import com.cloudproject.community_backend.entity.Post;
import com.cloudproject.community_backend.entity.User;
import com.cloudproject.community_backend.repository.PostRepository;
import com.cloudproject.community_backend.repository.UserRepository;
import com.cloudproject.community_backend.service.PostLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "게시물 좋아요/싫어요", description = "게시물 좋아요/싫어요 관련 API")
public class PostLikeController {

    private final PostLikeService postLikeService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @PostMapping("/{id}/like")
    @Operation(
        summary = "게시물 좋아요",
        description = "특정 게시물에 대해 좋아요를 누릅니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "게시물 좋아요 처리 완료",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{ \"message\": \"게시물 좋아요 처리 완료\", \"likes\": 12, \"dislikes\": 3 }")
            )
        ),
        @ApiResponse(responseCode = "404", description = "게시물 또는 유저를 찾을 수 없음")
    })
    public ResponseEntity<Map<String, Object>> likePost(
            @Parameter(description = "게시물 ID", example = "1") @PathVariable Long id,
            @Parameter(description = "유저 ID", example = "1") @RequestParam Long userId) {

        Post post = postRepository.findById(id).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        postLikeService.toggleLike(post, user, true);

        long likeCount = postLikeService.getLikeCount(post);
        long dislikeCount = postLikeService.getDislikeCount(post);

        return ResponseEntity.ok(Map.of(
                "message", "게시물 좋아요 처리 완료",
                "likes", likeCount,
                "dislikes", dislikeCount
        ));
    }

    @PostMapping("/{id}/dislike")
    @Operation(
        summary = "게시물 싫어요",
        description = "특정 게시물에 대해 싫어요를 누릅니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "게시물 싫어요 처리 완료",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{ \"message\": \"게시물 싫어요 처리 완료\", \"likes\": 10, \"dislikes\": 5 }")
            )
        ),
        @ApiResponse(responseCode = "404", description = "게시물 또는 유저를 찾을 수 없음")
    })
    public ResponseEntity<Map<String, Object>> dislikePost(
            @Parameter(description = "게시물 ID", example = "1") @PathVariable Long id,
            @Parameter(description = "유저 ID", example = "1") @RequestParam Long userId) {

        Post post = postRepository.findById(id).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        postLikeService.toggleLike(post, user, false);

        long likeCount = postLikeService.getLikeCount(post);
        long dislikeCount = postLikeService.getDislikeCount(post);

        return ResponseEntity.ok(Map.of(
                "message", "게시물 싫어요 처리 완료",
                "likes", likeCount,
                "dislikes", dislikeCount
        ));
    }

    @GetMapping("/{id}/likes")
    @Operation(
        summary = "게시물 좋아요/싫어요 수 조회",
        description = "특정 게시물의 좋아요/싫어요 개수를 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{ \"likes\": 12, \"dislikes\": 3 }")
            )
        ),
        @ApiResponse(responseCode = "404", description = "게시물을 찾을 수 없음")
    })
    public ResponseEntity<Map<String, Long>> getPostLikes(
            @Parameter(description = "게시물 ID", example = "1") @PathVariable Long id) {

        Post post = postRepository.findById(id).orElseThrow();
        long likeCount = postLikeService.getLikeCount(post);
        long dislikeCount = postLikeService.getDislikeCount(post);
        return ResponseEntity.ok(Map.of("likes", likeCount, "dislikes", dislikeCount));
    }
}
