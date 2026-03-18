package com.community.volunteer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.volunteer.entity.ServiceRecord;
import com.community.volunteer.mapper.ServiceRecordMapper;
import com.community.volunteer.service.ServiceRecordService;
import org.springframework.stereotype.Service;

@Service
public class ServiceRecordServiceImpl extends ServiceImpl<ServiceRecordMapper, ServiceRecord> implements ServiceRecordService {
}
