package com.community.volunteer.controller.statistics;

import com.community.volunteer.common.ApiResponse;
import com.community.volunteer.service.ServiceRecordService;
import com.community.volunteer.vo.common.PageVO;
import com.community.volunteer.vo.statistics.ServiceRecordVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/records")
public class ServiceRecordController {

    private final ServiceRecordService serviceRecordService;

    public ServiceRecordController(ServiceRecordService serviceRecordService) {
        this.serviceRecordService = serviceRecordService;
    }

    @GetMapping
    public ApiResponse<PageVO<ServiceRecordVO>> page(@RequestParam(defaultValue = "1") long pageNum,
                                                     @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(serviceRecordService.pageRecord(pageNum, pageSize));
    }
}
