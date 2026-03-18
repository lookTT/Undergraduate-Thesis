package com.community.volunteer.vo.activity;

import java.time.LocalDateTime;

public record ActivitySignupVO(
        Long id,
        Long activityId,
        Long volunteerId,
        LocalDateTime applyTime,
        Integer auditStatus,
        LocalDateTime auditTime,
        String auditRemark
) {
}
