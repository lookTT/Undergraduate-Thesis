package com.community.volunteer.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.volunteer.common.BusinessException;
import com.community.volunteer.common.ResultCode;
import com.community.volunteer.dto.volunteer.VolunteerSaveRequest;
import com.community.volunteer.entity.VolunteerProfile;
import com.community.volunteer.mapper.VolunteerProfileMapper;
import com.community.volunteer.service.VolunteerProfileService;
import com.community.volunteer.vo.common.PageVO;
import com.community.volunteer.vo.volunteer.VolunteerVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class VolunteerProfileServiceImpl extends ServiceImpl<VolunteerProfileMapper, VolunteerProfile> implements VolunteerProfileService {

    @Override
    public PageVO<VolunteerVO> pageVolunteer(long pageNum, long pageSize) {
        Page<VolunteerProfile> page = this.page(new Page<>(pageNum, pageSize));
        return new PageVO<>(
                page.getCurrent(),
                page.getSize(),
                page.getTotal(),
                page.getRecords().stream().map(this::toVO).toList()
        );
    }

    @Override
    public VolunteerVO getVolunteerById(Long id) {
        VolunteerProfile profile = this.getById(id);
        if (profile == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "志愿者档案不存在");
        }
        return toVO(profile);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveVolunteer(VolunteerSaveRequest request) {
        VolunteerProfile profile = new VolunteerProfile();
        profile.setUserId(request.userId());
        profile.setStudentOrMemberNo(request.studentOrMemberNo());
        profile.setGender(request.gender());
        profile.setAge(request.age());
        profile.setCommunityName(request.communityName());
        profile.setSkillTag(request.skillTag());
        profile.setRemark(request.remark());
        this.save(profile);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVolunteer(Long id, VolunteerSaveRequest request) {
        VolunteerProfile profile = this.getById(id);
        if (profile == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "志愿者档案不存在");
        }
        profile.setUserId(request.userId());
        profile.setStudentOrMemberNo(request.studentOrMemberNo());
        profile.setGender(request.gender());
        profile.setAge(request.age());
        profile.setCommunityName(request.communityName());
        profile.setSkillTag(request.skillTag());
        profile.setRemark(request.remark());
        this.updateById(profile);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVolunteer(Long id) {
        if (!this.removeById(id)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "志愿者档案不存在");
        }
    }

    private VolunteerVO toVO(VolunteerProfile profile) {
        return new VolunteerVO(
                profile.getId(),
                profile.getUserId(),
                profile.getStudentOrMemberNo(),
                profile.getGender(),
                profile.getAge(),
                profile.getCommunityName(),
                profile.getSkillTag(),
                profile.getRemark()
        );
    }
}
