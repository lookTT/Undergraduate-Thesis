package com.community.volunteer.vo.statistics;

import java.time.LocalDateTime;

public record ServiceRecordVO(
        Long id,
        Long activityId,
        Long volunteerId,
        Double serviceHours,
        String recordSource,
        LocalDateTime createTime
) {
}
