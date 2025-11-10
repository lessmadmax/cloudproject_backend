package com.cloudproject.community_backend.service;

import com.cloudproject.community_backend.entity.Comment;
import com.cloudproject.community_backend.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final GeminiService geminiService;

    public Comment createComment(Comment comment) {
        boolean isBad = geminiService.checkBadComment(
            comment.getContent(),
            comment.getAuthor().getUsername()   // ✅ 닉네임도 판별에 포함
        );
        comment.setBad(isBad);
        return commentRepository.save(comment);
    }

    public List<Comment> getBadComments() {
        return commentRepository.findAll()
                .stream()
                .filter(Comment::isBad)
                .toList();
    }
    
    

    public Iterable<Comment> getAllComments() {
        return commentRepository.findAll();
    }
}
