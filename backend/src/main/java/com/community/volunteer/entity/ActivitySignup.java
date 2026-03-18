package com.community.volunteer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("activity_signup")
public class ActivitySignup extends BaseEntity {

    private Long id;
    private Long activityId;
    private Long volunteerId;
    private LocalDateTime applyTime;
    private Integer auditStatus;
    private LocalDateTime auditTime;
    private String auditRemark;
}
