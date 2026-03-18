package com.community.volunteer.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.volunteer.entity.ServiceRecord;
import com.community.volunteer.mapper.ServiceRecordMapper;
import com.community.volunteer.service.ServiceRecordService;
import com.community.volunteer.vo.common.PageVO;
import com.community.volunteer.vo.statistics.ServiceRecordVO;
import org.springframework.stereotype.Service;

@Service
public class ServiceRecordServiceImpl extends ServiceImpl<ServiceRecordMapper, ServiceRecord> implements ServiceRecordService {

    public ServiceRecordServiceImpl(ServiceRecordMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public PageVO<ServiceRecordVO> pageRecord(long pageNum, long pageSize) {
        Page<ServiceRecord> page = this.page(new Page<>(pageNum, pageSize),
                Wrappers.<ServiceRecord>lambdaQuery().orderByDesc(ServiceRecord::getCreateTime));
        return new PageVO<>(
                page.getCurrent(),
                page.getSize(),
                page.getTotal(),
                page.getRecords().stream().map(this::toVO).toList()
        );
    }

    private ServiceRecordVO toVO(ServiceRecord record) {
        return new ServiceRecordVO(
                record.getId(),
                record.getActivityId(),
                record.getVolunteerId(),
                record.getServiceHours(),
                record.getRecordSource(),
                record.getCreateTime()
        );
    }
}
