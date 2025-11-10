package com.cloudproject.community_backend.service;

import com.cloudproject.community_backend.controller.PostController.MeetingInfo;
import com.cloudproject.community_backend.controller.PostController.PostCreateRequest;
import com.cloudproject.community_backend.controller.PostController.QuestionInfo;
import com.cloudproject.community_backend.entity.MeetingPostDetail;
import com.cloudproject.community_backend.entity.Post;
import com.cloudproject.community_backend.entity.PostBoardType;
import com.cloudproject.community_backend.entity.QuestionPostDetail;
import com.cloudproject.community_backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final GeminiService geminiService;

    private static final DateTimeFormatter ISO_LOCAL_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Post createPost(Post post, PostCreateRequest request) {
        boolean isBad = geminiService.checkBadComment(
                post.getTitle() + " " + post.getContent(),
                post.getAuthor().getUsername()
        );

        post.setBad(isBad);

        if (post.getBoardType() == PostBoardType.MEETING) {
            MeetingInfo meetingInfo = request.meetingInfo();
            if (meetingInfo == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "모임 게시판에는 모임 정보가 필요합니다.");
            }

            MeetingPostDetail detail = new MeetingPostDetail();
            detail.setPost(post);

            if (meetingInfo.schedule() != null && !meetingInfo.schedule().isBlank()) {
                detail.setSchedule(LocalDateTime.parse(meetingInfo.schedule(), ISO_LOCAL_DATE_TIME));
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "모임 일정이 필요합니다.");
            }

            detail.setLocation(meetingInfo.location());
            detail.setCapacity(meetingInfo.capacity());
            post.setMeetingDetails(detail);
        } else if (post.getBoardType() == PostBoardType.QUESTION) {
            QuestionInfo questionInfo = request.questionInfo();
            if (questionInfo == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "질문 게시판에는 질문 정보가 필요합니다.");
            }

            QuestionPostDetail detail = new QuestionPostDetail();
            detail.setPost(post);
            detail.setCategoryName(questionInfo.categoryName());
            detail.setForSeniorsOnly(questionInfo.isForSeniorsOnly());
            post.setQuestionDetails(detail);
        }

        return postRepository.save(post);
    }
}

