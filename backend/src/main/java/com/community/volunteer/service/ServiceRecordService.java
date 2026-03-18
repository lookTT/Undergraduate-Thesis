package com.community.volunteer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.volunteer.entity.ServiceRecord;
import com.community.volunteer.vo.common.PageVO;
import com.community.volunteer.vo.statistics.ServiceRecordVO;

public interface ServiceRecordService extends IService<ServiceRecord> {

    PageVO<ServiceRecordVO> pageRecord(long pageNum, long pageSize);
}
