package com.community.volunteer.vo.activity;

import java.time.LocalDateTime;

public record ActivityCheckinVO(
        Long id,
        Long activityId,
        Long volunteerId,
        LocalDateTime checkinTime,
        Integer checkinStatus,
        String checkinNote
) {
}
