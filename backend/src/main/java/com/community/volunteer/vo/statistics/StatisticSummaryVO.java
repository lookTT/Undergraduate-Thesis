package com.community.volunteer.vo.statistics;

public record StatisticSummaryVO(
        Long volunteerCount,
        Long activityCount,
        Long signupCount,
        Double serviceHours
) {
}
