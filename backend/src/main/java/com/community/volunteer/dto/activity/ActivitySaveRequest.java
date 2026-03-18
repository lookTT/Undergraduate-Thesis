package com.community.volunteer.dto.activity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ActivitySaveRequest(
        @NotBlank(message = "活动标题不能为空")
        String title,
        String content,
        @NotBlank(message = "活动地点不能为空")
        String location,
        @NotNull(message = "开始时间不能为空")
        LocalDateTime startTime,
        @NotNull(message = "结束时间不能为空")
        LocalDateTime endTime,
        @NotNull(message = "招募人数不能为空")
        Integer recruitCount
) {
}
