package com.community.volunteer.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.volunteer.common.BusinessException;
import com.community.volunteer.common.ResultCode;
import com.community.volunteer.dto.activity.ActivitySaveRequest;
import com.community.volunteer.entity.Activity;
import com.community.volunteer.mapper.ActivityMapper;
import com.community.volunteer.service.ActivityService;
import com.community.volunteer.vo.activity.ActivityVO;
import com.community.volunteer.vo.common.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    @Override
    public PageVO<ActivityVO> pageActivity(long pageNum, long pageSize) {
        Page<Activity> page = this.page(new Page<>(pageNum, pageSize));
        return new PageVO<>(
                page.getCurrent(),
                page.getSize(),
                page.getTotal(),
                page.getRecords().stream().map(this::toVO).toList()
        );
    }

    @Override
    public ActivityVO getActivityById(Long id) {
        Activity activity = this.getById(id);
        if (activity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "活动不存在");
        }
        return toVO(activity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveActivity(ActivitySaveRequest request, Long creatorId) {
        validateTime(request);
        Activity activity = new Activity();
        activity.setTitle(request.title());
        activity.setContent(request.content());
        activity.setLocation(request.location());
        activity.setStartTime(request.startTime());
        activity.setEndTime(request.endTime());
        activity.setRecruitCount(request.recruitCount());
        activity.setStatus(0);
        activity.setCreatorId(creatorId);
        this.save(activity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActivity(Long id, ActivitySaveRequest request) {
        validateTime(request);
        Activity activity = this.getById(id);
        if (activity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "活动不存在");
        }
        activity.setTitle(request.title());
        activity.setContent(request.content());
        activity.setLocation(request.location());
        activity.setStartTime(request.startTime());
        activity.setEndTime(request.endTime());
        activity.setRecruitCount(request.recruitCount());
        this.updateById(activity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteActivity(Long id) {
        if (!this.removeById(id)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "活动不存在");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishActivity(Long id) {
        Activity activity = this.getById(id);
        if (activity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "活动不存在");
        }
        activity.setStatus(1);
        this.updateById(activity);
    }

    private void validateTime(ActivitySaveRequest request) {
        if (Objects.nonNull(request.startTime()) && Objects.nonNull(request.endTime())
                && request.endTime().isBefore(request.startTime())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "结束时间不能早于开始时间");
        }
    }

    private ActivityVO toVO(Activity activity) {
        return new ActivityVO(
                activity.getId(),
                activity.getTitle(),
                activity.getContent(),
                activity.getLocation(),
                activity.getStartTime(),
                activity.getEndTime(),
                activity.getRecruitCount(),
                activity.getStatus(),
                activity.getCreatorId()
        );
    }
}
