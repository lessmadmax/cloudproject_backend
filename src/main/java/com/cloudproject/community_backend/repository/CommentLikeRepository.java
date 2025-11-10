package com.cloudproject.community_backend.repository;

import com.cloudproject.community_backend.entity.Comment;
import com.cloudproject.community_backend.entity.CommentLike;
import com.cloudproject.community_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);
    long countByCommentAndLiked(Comment comment, boolean liked);


}
