package com.cloudproject.community_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MetricDTO {
    private String id;            // 고유 식별자
    private String title;         // 표시 제목
    private Object value;         // 현재 값 (숫자 또는 문자열)
    private String sub;           // 하위 레이블
    private String trend;         // "up", "down", "neutral"
    private String trendValue;    // 추세 크기
    private String status;        // "good", "warning", "bad"
    private boolean isHighlight;  // 강조 카드 여부
    private Double progressBar;   // 0-100
    private List<Number> history; // 최근 7일
}


