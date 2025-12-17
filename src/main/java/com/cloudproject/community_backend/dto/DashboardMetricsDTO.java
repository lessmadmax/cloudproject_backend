package com.cloudproject.community_backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardMetricsDTO {

    private TrustMetricsDTO trust;
    private MentorMetricsDTO mentor;
    private ValueMetricsDTO value;

    @Data
    @Builder
    public static class TrustMetricsDTO {
        private MetricDTO autoBlockPrecision;
        private MetricDTO incidentRate;
        private MetricDTO appealOverturnRate;
        private MetricDTO timeToReview;
    }

    @Data
    @Builder
    public static class MentorMetricsDTO {
        private MetricDTO pac;
        private MetricDTO unanswered;
        private MetricDTO tfpa;
        private MetricDTO phr; // 요구사항상 PHR=답변 유무 기반으로 판단
    }

    @Data
    @Builder
    public static class ValueMetricsDTO {
        private MetricDTO rqr;
        private MetricDTO aiResolution;
        private MetricDTO peerResolution;
    }
}


