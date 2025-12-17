package com.cloudproject.community_backend.repository;

import com.cloudproject.community_backend.entity.PostBoardType;
import com.cloudproject.community_backend.entity.ReportStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AdminMetricQueryRepository {

    @PersistenceContext
    private EntityManager em;

    public long countPosts(LocalDateTime start, LocalDateTime end) {
        return em.createQuery(
                "select count(p) from Post p where p.createdAt between :s and :e",
                Long.class
            )
            .setParameter("s", start)
            .setParameter("e", end)
            .getSingleResult();
    }

    public long countBadPosts(LocalDateTime start, LocalDateTime end) {
        return em.createQuery(
                "select count(p) from Post p where p.isBad = true and p.createdAt between :s and :e",
                Long.class
            )
            .setParameter("s", start)
            .setParameter("e", end)
            .getSingleResult();
    }

    public long countComments(LocalDateTime start, LocalDateTime end) {
        return em.createQuery(
                "select count(c) from Comment c where c.createdAt between :s and :e",
                Long.class
            )
            .setParameter("s", start)
            .setParameter("e", end)
            .getSingleResult();
    }

    public long countBadComments(LocalDateTime start, LocalDateTime end) {
        return em.createQuery(
                "select count(c) from Comment c where c.isBad = true and c.createdAt between :s and :e",
                Long.class
            )
            .setParameter("s", start)
            .setParameter("e", end)
            .getSingleResult();
    }

    public long countQuestions(LocalDateTime start, LocalDateTime end) {
        return em.createQuery(
                "select count(p) from Post p where p.boardType = :bt and p.createdAt between :s and :e",
                Long.class
            )
            .setParameter("bt", PostBoardType.QUESTION)
            .setParameter("s", start)
            .setParameter("e", end)
            .getSingleResult();
    }

    /**
     * 최근 범위 내 "질문 글" 중, 선배가 작성한 댓글이 1개라도 달린 질문 수 (선배 답변 유무 기준)
     */
    public long countAnsweredQuestions(LocalDateTime start, LocalDateTime end) {
        return em.createQuery(
                """
                select count(p)
                from Post p
                where p.boardType = :bt
                  and p.createdAt between :s and :e
                  and exists (
                      select 1 
                      from Comment c 
                      where c.post = p 
                        and c.author.isSeniorVerified = true
                  )
                """,
                Long.class
            )
            .setParameter("bt", PostBoardType.QUESTION)
            .setParameter("s", start)
            .setParameter("e", end)
            .getSingleResult();
    }

    /**
     * 질문별 createdAt + 선배의 첫 답변 시간(firstAnswerAt = min senior comment.createdAt)을 한번에 가져옴
     * row: [postId, postCreatedAt, firstAnswerAt]
     */
    public List<Object[]> findQuestionFirstAnswerRows(LocalDateTime start, LocalDateTime end) {
        return em.createQuery(
                """
                select p.id, p.createdAt, min(c.createdAt)
                from Post p
                left join Comment c on c.post = p and c.author.isSeniorVerified = true
                where p.boardType = :bt
                  and p.createdAt between :s and :e
                group by p.id, p.createdAt
                having min(c.createdAt) is not null
                """,
                Object[].class
            )
            .setParameter("bt", PostBoardType.QUESTION)
            .setParameter("s", start)
            .setParameter("e", end)
            .getResultList();
    }

    public List<Object[]> findReviewedReports(LocalDateTime start, LocalDateTime end) {
        return em.createQuery(
                """
                select r.createdAt, r.reviewedAt
                from Report r
                where r.status in :statuses
                  and r.reviewedAt is not null
                  and r.reviewedAt between :s and :e
                """,
                Object[].class
            )
            .setParameter("statuses", List.of(ReportStatus.APPROVED, ReportStatus.REJECTED))
            .setParameter("s", start)
            .setParameter("e", end)
            .getResultList();
    }
}


