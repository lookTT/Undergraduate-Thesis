package com.community.volunteer.vo.activity;

import java.time.LocalDateTime;

public record ActivityVO(
        Long id,
        String title,
        String content,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer recruitCount,
        Integer status,
        Long creatorId
) {
}
