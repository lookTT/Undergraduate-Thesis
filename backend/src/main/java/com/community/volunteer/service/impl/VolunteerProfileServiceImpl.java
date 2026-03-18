package com.community.volunteer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.volunteer.entity.VolunteerProfile;
import com.community.volunteer.mapper.VolunteerProfileMapper;
import com.community.volunteer.service.VolunteerProfileService;
import org.springframework.stereotype.Service;

@Service
public class VolunteerProfileServiceImpl extends ServiceImpl<VolunteerProfileMapper, VolunteerProfile> implements VolunteerProfileService {
}
