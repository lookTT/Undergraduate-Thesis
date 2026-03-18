package com.community.volunteer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("volunteer_profile")
public class VolunteerProfile extends BaseEntity {

    private Long id;
    private Long userId;
    private String studentOrMemberNo;
    private String gender;
    private Integer age;
    private String communityName;
    private String skillTag;
    private String remark;
}
