package com.community.volunteer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.volunteer.entity.ActivityCheckin;
import com.community.volunteer.mapper.ActivityCheckinMapper;
import com.community.volunteer.service.ActivityCheckinService;
import org.springframework.stereotype.Service;

@Service
public class ActivityCheckinServiceImpl extends ServiceImpl<ActivityCheckinMapper, ActivityCheckin> implements ActivityCheckinService {
}
