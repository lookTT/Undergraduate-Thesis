package com.community.volunteer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.volunteer.dto.volunteer.VolunteerSaveRequest;
import com.community.volunteer.vo.common.PageVO;
import com.community.volunteer.vo.volunteer.VolunteerVO;

public interface VolunteerProfileService extends IService<com.community.volunteer.entity.VolunteerProfile> {

    PageVO<VolunteerVO> pageVolunteer(long pageNum, long pageSize);

    VolunteerVO getVolunteerById(Long id);

    void saveVolunteer(VolunteerSaveRequest request);

    void updateVolunteer(Long id, VolunteerSaveRequest request);

    void deleteVolunteer(Long id);
}
