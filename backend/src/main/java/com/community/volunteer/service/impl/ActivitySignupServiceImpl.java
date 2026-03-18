package com.community.volunteer.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.volunteer.entity.ActivitySignup;
import com.community.volunteer.mapper.ActivitySignupMapper;
import com.community.volunteer.mapper.ActivityMapper;
import com.community.volunteer.mapper.VolunteerProfileMapper;
import com.community.volunteer.common.BusinessException;
import com.community.volunteer.common.ResultCode;
import com.community.volunteer.entity.Activity;
import com.community.volunteer.entity.VolunteerProfile;
import com.community.volunteer.service.ActivitySignupService;
import com.community.volunteer.vo.activity.ActivitySignupVO;
import com.community.volunteer.vo.common.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ActivitySignupServiceImpl extends ServiceImpl<ActivitySignupMapper, ActivitySignup> implements ActivitySignupService {

    private final ActivityMapper activityMapper;
    private final VolunteerProfileMapper volunteerProfileMapper;

    public ActivitySignupServiceImpl(ActivitySignupMapper baseMapper,
                                     ActivityMapper activityMapper,
                                     VolunteerProfileMapper volunteerProfileMapper) {
        this.baseMapper = baseMapper;
        this.activityMapper = activityMapper;
        this.volunteerProfileMapper = volunteerProfileMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void signup(Long activityId, Long volunteerId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "活动不存在");
        }
        if (activity.getStatus() == null || activity.getStatus() != 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "活动未发布，无法报名");
        }
        VolunteerProfile profile = volunteerProfileMapper.selectById(volunteerId);
        if (profile == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "志愿者档案不存在");
        }
        ActivitySignup existing = this.getOne(Wrappers.<ActivitySignup>lambdaQuery()
                .eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getVolunteerId, volunteerId)
                .last("limit 1"));
        if (existing != null) {
            throw new BusinessException(ResultCode.CONFLICT, "已经报名过该活动");
        }

        ActivitySignup signup = new ActivitySignup();
        signup.setActivityId(activityId);
        signup.setVolunteerId(volunteerId);
        signup.setApplyTime(LocalDateTime.now());
        signup.setAuditStatus(0);
        this.save(signup);
    }

    @Override
    public PageVO<ActivitySignupVO> pageByActivity(Long activityId, long pageNum, long pageSize) {
        Page<ActivitySignup> page = this.page(new Page<>(pageNum, pageSize),
                Wrappers.<ActivitySignup>lambdaQuery()
                        .eq(ActivitySignup::getActivityId, activityId)
                        .orderByDesc(ActivitySignup::getApplyTime));
        return new PageVO<>(
                page.getCurrent(),
                page.getSize(),
                page.getTotal(),
                page.getRecords().stream().map(this::toVO).toList()
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long signupId) {
        ActivitySignup signup = this.getById(signupId);
        if (signup == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "报名记录不存在");
        }
        signup.setAuditStatus(1);
        signup.setAuditTime(LocalDateTime.now());
        signup.setAuditRemark("审核通过");
        this.updateById(signup);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long signupId) {
        ActivitySignup signup = this.getById(signupId);
        if (signup == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "报名记录不存在");
        }
        signup.setAuditStatus(2);
        signup.setAuditTime(LocalDateTime.now());
        signup.setAuditRemark("审核拒绝");
        this.updateById(signup);
    }

    private ActivitySignupVO toVO(ActivitySignup signup) {
        return new ActivitySignupVO(
                signup.getId(),
                signup.getActivityId(),
                signup.getVolunteerId(),
                signup.getApplyTime(),
                signup.getAuditStatus(),
                signup.getAuditTime(),
                signup.getAuditRemark()
        );
    }
}
