package com.community.volunteer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.volunteer.dto.activity.ActivitySaveRequest;
import com.community.volunteer.vo.activity.ActivityVO;
import com.community.volunteer.vo.common.PageVO;

public interface ActivityService extends IService<com.community.volunteer.entity.Activity> {

    PageVO<ActivityVO> pageActivity(long pageNum, long pageSize);

    ActivityVO getActivityById(Long id);

    void saveActivity(ActivitySaveRequest request, Long creatorId);

    void updateActivity(Long id, ActivitySaveRequest request);

    void deleteActivity(Long id);

    void publishActivity(Long id);
}
