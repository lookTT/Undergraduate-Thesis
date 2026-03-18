package com.community.volunteer.dto.activity;

import jakarta.validation.constraints.NotNull;

public record ActivitySignupRequest(
        @NotNull(message = "活动ID不能为空")
        Long activityId
) {
}
