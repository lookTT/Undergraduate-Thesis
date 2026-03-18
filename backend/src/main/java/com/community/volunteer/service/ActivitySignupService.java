package com.community.volunteer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.volunteer.entity.ActivitySignup;
import com.community.volunteer.vo.activity.ActivitySignupVO;
import com.community.volunteer.vo.common.PageVO;

public interface ActivitySignupService extends IService<ActivitySignup> {

    void signup(Long activityId, Long volunteerId);

    PageVO<ActivitySignupVO> pageByActivity(Long activityId, long pageNum, long pageSize);

    void approve(Long signupId);

    void reject(Long signupId);
}
