package com.community.volunteer.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.volunteer.common.BusinessException;
import com.community.volunteer.common.ResultCode;
import com.community.volunteer.entity.Activity;
import com.community.volunteer.entity.ActivityCheckin;
import com.community.volunteer.entity.ActivitySignup;
import com.community.volunteer.entity.ServiceRecord;
import com.community.volunteer.mapper.ActivityCheckinMapper;
import com.community.volunteer.mapper.ActivityMapper;
import com.community.volunteer.mapper.ActivitySignupMapper;
import com.community.volunteer.mapper.ServiceRecordMapper;
import com.community.volunteer.service.ActivityCheckinService;
import com.community.volunteer.vo.activity.ActivityCheckinVO;
import com.community.volunteer.vo.common.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class ActivityCheckinServiceImpl extends ServiceImpl<ActivityCheckinMapper, ActivityCheckin> implements ActivityCheckinService {

    private final ActivityMapper activityMapper;
    private final ActivitySignupMapper activitySignupMapper;
    private final ServiceRecordMapper serviceRecordMapper;

    public ActivityCheckinServiceImpl(ActivityCheckinMapper baseMapper,
                                      ActivityMapper activityMapper,
                                      ActivitySignupMapper activitySignupMapper,
                                      ServiceRecordMapper serviceRecordMapper) {
        this.baseMapper = baseMapper;
        this.activityMapper = activityMapper;
        this.activitySignupMapper = activitySignupMapper;
        this.serviceRecordMapper = serviceRecordMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkin(Long activityId, Long volunteerId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "活动不存在");
        }
        ActivitySignup signup = activitySignupMapper.selectOne(Wrappers.<ActivitySignup>lambdaQuery()
                .eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getVolunteerId, volunteerId)
                .eq(ActivitySignup::getAuditStatus, 1)
                .last("limit 1"));
        if (signup == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "未审核通过，无法签到");
        }
        ActivityCheckin existing = this.getOne(Wrappers.<ActivityCheckin>lambdaQuery()
                .eq(ActivityCheckin::getActivityId, activityId)
                .eq(ActivityCheckin::getVolunteerId, volunteerId)
                .last("limit 1"));
        if (existing != null) {
            throw new BusinessException(ResultCode.CONFLICT, "已经签到过该活动");
        }

        ActivityCheckin checkin = new ActivityCheckin();
        checkin.setActivityId(activityId);
        checkin.setVolunteerId(volunteerId);
        checkin.setCheckinTime(LocalDateTime.now());
        checkin.setCheckinStatus(1);
        checkin.setCheckinNote("签到成功");
        this.save(checkin);

        ServiceRecord record = new ServiceRecord();
        record.setActivityId(activityId);
        record.setVolunteerId(volunteerId);
        record.setServiceHours(calcHours(activity));
        record.setRecordSource("CHECKIN");
        serviceRecordMapper.insert(record);
    }

    @Override
    public PageVO<ActivityCheckinVO> pageByActivity(Long activityId, long pageNum, long pageSize) {
        Page<ActivityCheckin> page = this.page(new Page<>(pageNum, pageSize),
                Wrappers.<ActivityCheckin>lambdaQuery()
                        .eq(ActivityCheckin::getActivityId, activityId)
                        .orderByDesc(ActivityCheckin::getCheckinTime));
        return new PageVO<>(
                page.getCurrent(),
                page.getSize(),
                page.getTotal(),
                page.getRecords().stream().map(this::toVO).toList()
        );
    }

    private Double calcHours(Activity activity) {
        if (activity.getStartTime() == null || activity.getEndTime() == null) {
            return 0.0;
        }
        long minutes = ChronoUnit.MINUTES.between(activity.getStartTime(), activity.getEndTime());
        return Math.max(0.0, minutes / 60.0);
    }

    private ActivityCheckinVO toVO(ActivityCheckin checkin) {
        return new ActivityCheckinVO(
                checkin.getId(),
                checkin.getActivityId(),
                checkin.getVolunteerId(),
                checkin.getCheckinTime(),
                checkin.getCheckinStatus(),
                checkin.getCheckinNote()
        );
    }
}
