package com.community.volunteer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.volunteer.entity.ActivityCheckin;
import com.community.volunteer.vo.activity.ActivityCheckinVO;
import com.community.volunteer.vo.common.PageVO;

public interface ActivityCheckinService extends IService<ActivityCheckin> {

    void checkin(Long activityId, Long volunteerId);

    PageVO<ActivityCheckinVO> pageByActivity(Long activityId, long pageNum, long pageSize);
}
