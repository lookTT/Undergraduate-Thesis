package com.community.volunteer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity {

    private Long id;
    private String username;
    private String passwordHash;
    private String realName;
    private String phone;
    private Integer status;
}
