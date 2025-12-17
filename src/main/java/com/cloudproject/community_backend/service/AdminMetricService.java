package com.cloudproject.community_backend.service;

import com.cloudproject.community_backend.dto.DashboardMetricsDTO;
import com.cloudproject.community_backend.dto.MetricDTO;
import com.cloudproject.community_backend.repository.AdminMetricQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMetricService {

    private final AdminMetricQueryRepository q;

    @Transactional(readOnly = true)
    public DashboardMetricsDTO getCurrentMetrics() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);
        LocalDateTime windowStart = startDate.atStartOfDay();
        LocalDateTime windowEnd = today.plusDays(1).atStartOfDay();

        // --- Mentor/Value: "답변" = 질문글에 달린 댓글 존재 여부 ---
        long totalQuestions = q.countQuestions(windowStart, windowEnd);
        long answeredQuestions = q.countAnsweredQuestions(windowStart, windowEnd);
        long unanswered = Math.max(0, totalQuestions - answeredQuestions);

        List<Number> pacHistory = new ArrayList<>();
        List<Number> unansweredHistory = new ArrayList<>();
        List<Number> tfpaHistoryHours = new ArrayList<>();

        for (LocalDate d = startDate; !d.isAfter(today); d = d.plusDays(1)) {
            LocalDateTime s = d.atStartOfDay();
            LocalDateTime e = d.plusDays(1).atStartOfDay();
            long dq = q.countQuestions(s, e);
            long da = q.countAnsweredQuestions(s, e);
            pacHistory.add(round1(dq > 0 ? (da * 100.0 / dq) : 0.0));
            unansweredHistory.add(Math.max(0, dq - da));
            tfpaHistoryHours.add(round1(computeTfpaHours(s, e)));
        }

        double pac = totalQuestions > 0 ? (answeredQuestions * 100.0 / totalQuestions) : 0.0;
        MetricDTO pacMetric = MetricDTO.builder()
            .id("pac")
            .title("선배 답변 커버리지")
            .value(formatPercent(pac))
            .sub(String.format("%d / %d questions", answeredQuestions, totalQuestions))
            .trend(trendFromHistory(pacHistory))
            .trendValue(trendValueFromHistory(pacHistory, "%"))
            .status(statusForHigherBetterPercent(pac, 70, 40))
            .progressBar(pac)
            .history(pacHistory)
            .isHighlight(true)
            .build();

        MetricDTO unansweredMetric = MetricDTO.builder()
            .id("unanswered")
            .title("미답변 백로그")
            .value(unanswered)
            .sub("최근 7일 질문 기준")
            .trend(trendFromHistory(unansweredHistory))
            .trendValue(trendValueFromHistory(unansweredHistory, ""))
            .status(statusForLowerBetter(unanswered, 0, 5))
            .history(unansweredHistory)
            .build();

        double tfpaHours = computeTfpaHours(windowStart, windowEnd);
        MetricDTO tfpaMetric = MetricDTO.builder()
            .id("tfpa")
            .title("첫 선배 답변 시간")
            .value(tfpaHours == 0.0 ? "N/A" : formatHours(tfpaHours))
            .sub("최근 7일 질문 기준")
            .trend(trendFromHistory(tfpaHistoryHours))
            .trendValue(trendValueFromHistory(tfpaHistoryHours, "h"))
            .status(tfpaHours == 0.0 ? "warning" : statusForLowerBetter(tfpaHours, 2.0, 12.0))
            .history(tfpaHistoryHours)
            .build();

        // 요구: PHR = 답변 유무로 판단 => 실질적으로 PAC와 동일한 신호
        MetricDTO phrMetric = MetricDTO.builder()
            .id("phr")
            .title("동료 도움률")
            .value(formatPercent(pac))
            .sub("답변 유무 기반")
            .trend(trendFromHistory(pacHistory))
            .trendValue(trendValueFromHistory(pacHistory, "%"))
            .status(statusForHigherBetterPercent(pac, 70, 40))
            .progressBar(pac)
            .history(pacHistory)
            .build();

        // 해결 질문 비율(RQR): "답변 달리면 해결" => PAC와 동일
        MetricDTO rqrMetric = MetricDTO.builder()
            .id("rqr")
            .title("해결 질문 비율")
            .value(formatPercent(pac))
            .sub(String.format("%d / %d questions", answeredQuestions, totalQuestions))
            .trend(trendFromHistory(pacHistory))
            .trendValue(trendValueFromHistory(pacHistory, "%"))
            .status(statusForHigherBetterPercent(pac, 70, 40))
            .progressBar(pac)
            .history(pacHistory)
            .build();

        MetricDTO aiResolutionMetric = MetricDTO.builder()
            .id("ai_resolution")
            .title("AI 기여도")
            .value("N/A")
            .sub("AI 답변 저장/매핑 미구현")
            .trend("neutral")
            .trendValue("")
            .status("warning")
            .history(List.of())
            .build();

        MetricDTO peerResolutionMetric = MetricDTO.builder()
            .id("peer_resolution")
            .title("동료 기여도")
            .value(formatPercent(pac))
            .sub("답변 유무 기반")
            .trend(trendFromHistory(pacHistory))
            .trendValue(trendValueFromHistory(pacHistory, "%"))
            .status(statusForHigherBetterPercent(pac, 70, 40))
            .progressBar(pac)
            .history(pacHistory)
            .build();

        // --- Trust ---
        long postsTotal = q.countPosts(windowStart, windowEnd);
        long postsBad = q.countBadPosts(windowStart, windowEnd);
        long commentsTotal = q.countComments(windowStart, windowEnd);
        long commentsBad = q.countBadComments(windowStart, windowEnd);
        double incidentRate = (postsTotal + commentsTotal) > 0
            ? ((postsBad + commentsBad) * 100.0 / (postsTotal + commentsTotal))
            : 0.0;

        List<Number> incidentHistory = new ArrayList<>();
        for (LocalDate d = startDate; !d.isAfter(today); d = d.plusDays(1)) {
            LocalDateTime s = d.atStartOfDay();
            LocalDateTime e = d.plusDays(1).atStartOfDay();
            long pT = q.countPosts(s, e);
            long pB = q.countBadPosts(s, e);
            long cT = q.countComments(s, e);
            long cB = q.countBadComments(s, e);
            incidentHistory.add(round1((pT + cT) > 0 ? ((pB + cB) * 100.0 / (pT + cT)) : 0.0));
        }

        MetricDTO incidentMetric = MetricDTO.builder()
            .id("incident_rate")
            .title("불량률")
            .value(formatPercent(incidentRate))
            .sub(String.format("%d bad / %d total", (postsBad + commentsBad), (postsTotal + commentsTotal)))
            .trend(trendFromHistory(incidentHistory))
            .trendValue(trendValueFromHistory(incidentHistory, "%"))
            .status(statusForLowerBetter(incidentRate, 5.0, 10.0))
            .progressBar(Math.min(100.0, incidentRate))
            .history(incidentHistory)
            .build();

        double timeToReviewHours = averageReviewHours(windowStart, windowEnd);
        List<Number> reviewHistory = new ArrayList<>();
        for (LocalDate d = startDate; !d.isAfter(today); d = d.plusDays(1)) {
            LocalDateTime s = d.atStartOfDay();
            LocalDateTime e = d.plusDays(1).atStartOfDay();
            reviewHistory.add(round1(averageReviewHours(s, e)));
        }

        MetricDTO timeToReviewMetric = MetricDTO.builder()
            .id("time_to_review")
            .title("검토 시간")
            .value(timeToReviewHours == 0.0 ? "N/A" : formatHours(timeToReviewHours))
            .sub("승인/반려 처리 평균")
            .trend(trendFromHistory(reviewHistory))
            .trendValue(trendValueFromHistory(reviewHistory, "h"))
            .status(timeToReviewHours == 0.0 ? "warning" : statusForLowerBetter(timeToReviewHours, 12.0, 24.0))
            .history(reviewHistory)
            .build();

        MetricDTO autoBlockPrecisionMetric = MetricDTO.builder()
            .id("auto_block_precision")
            .title("자동차단 정확도")
            .value("N/A")
            .sub("차단 출처/번복 이력 미구현")
            .trend("neutral")
            .trendValue("")
            .status("warning")
            .history(List.of())
            .build();

        MetricDTO appealOverturnMetric = MetricDTO.builder()
            .id("appeal_overturn_rate")
            .title("이의 제기 번복률")
            .value("N/A")
            .sub("이의 제기 기능 미구현")
            .trend("neutral")
            .trendValue("")
            .status("warning")
            .history(List.of())
            .build();

        return DashboardMetricsDTO.builder()
            .trust(DashboardMetricsDTO.TrustMetricsDTO.builder()
                .autoBlockPrecision(autoBlockPrecisionMetric)
                .incidentRate(incidentMetric)
                .appealOverturnRate(appealOverturnMetric)
                .timeToReview(timeToReviewMetric)
                .build())
            .mentor(DashboardMetricsDTO.MentorMetricsDTO.builder()
                .pac(pacMetric)
                .unanswered(unansweredMetric)
                .tfpa(tfpaMetric)
                .phr(phrMetric)
                .build())
            .value(DashboardMetricsDTO.ValueMetricsDTO.builder()
                .rqr(rqrMetric)
                .aiResolution(aiResolutionMetric)
                .peerResolution(peerResolutionMetric)
                .build())
            .build();
    }

    private double computeTfpaHours(LocalDateTime start, LocalDateTime end) {
        List<Object[]> rows = q.findQuestionFirstAnswerRows(start, end);
        if (rows.isEmpty()) return 0.0;

        double totalHours = 0.0;
        long count = 0;
        for (Object[] row : rows) {
            LocalDateTime questionAt = (LocalDateTime) row[1];
            LocalDateTime firstAnswerAt = (LocalDateTime) row[2];
            if (questionAt != null && firstAnswerAt != null && !firstAnswerAt.isBefore(questionAt)) {
                totalHours += Duration.between(questionAt, firstAnswerAt).toMinutes() / 60.0;
                count++;
            }
        }
        return count > 0 ? (totalHours / count) : 0.0;
    }

    private double averageReviewHours(LocalDateTime start, LocalDateTime end) {
        List<Object[]> rows = q.findReviewedReports(start, end);
        if (rows.isEmpty()) return 0.0;
        double total = 0.0;
        long count = 0;
        for (Object[] r : rows) {
            LocalDateTime createdAt = (LocalDateTime) r[0];
            LocalDateTime reviewedAt = (LocalDateTime) r[1];
            if (createdAt != null && reviewedAt != null && !reviewedAt.isBefore(createdAt)) {
                total += Duration.between(createdAt, reviewedAt).toMinutes() / 60.0;
                count++;
            }
        }
        return count > 0 ? (total / count) : 0.0;
    }

    private static double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }

    private static String formatPercent(double v) {
        return String.format("%.1f%%", v);
    }

    private static String formatHours(double hours) {
        return String.format("%.1fh", hours);
    }

    private static String trendFromHistory(List<Number> history) {
        if (history == null || history.size() < 2) return "neutral";
        double first = history.get(0).doubleValue();
        double last = history.get(history.size() - 1).doubleValue();
        if (last > first) return "up";
        if (last < first) return "down";
        return "neutral";
    }

    private static String trendValueFromHistory(List<Number> history, String unitSuffix) {
        if (history == null || history.size() < 2) return "";
        double first = history.get(0).doubleValue();
        double last = history.get(history.size() - 1).doubleValue();
        double delta = Math.abs(last - first);
        if ("%".equals(unitSuffix)) return String.format("%.1f%%", delta);
        if ("h".equals(unitSuffix)) return String.format("%.1fh", delta);
        return String.format("%.0f", delta);
    }

    private static String statusForHigherBetterPercent(double value, double goodThreshold, double warningThreshold) {
        if (value >= goodThreshold) return "good";
        if (value >= warningThreshold) return "warning";
        return "bad";
    }

    private static String statusForLowerBetter(double value, double goodThreshold, double warningThreshold) {
        if (value <= goodThreshold) return "good";
        if (value <= warningThreshold) return "warning";
        return "bad";
    }
}


