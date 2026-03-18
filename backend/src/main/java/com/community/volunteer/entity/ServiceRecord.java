package com.community.volunteer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("service_record")
public class ServiceRecord extends BaseEntity {

    private Long id;
    private Long activityId;
    private Long volunteerId;
    private Double serviceHours;
    private String recordSource;
}
