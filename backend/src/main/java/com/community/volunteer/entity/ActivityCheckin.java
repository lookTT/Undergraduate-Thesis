package com.community.volunteer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("activity_checkin")
public class ActivityCheckin extends BaseEntity {

    private Long id;
    private Long activityId;
    private Long volunteerId;
    private LocalDateTime checkinTime;
    private Integer checkinStatus;
    private String checkinNote;
}
