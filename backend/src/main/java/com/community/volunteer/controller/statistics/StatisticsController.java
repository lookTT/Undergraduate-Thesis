package com.community.volunteer.controller.statistics;

import com.community.volunteer.common.ApiResponse;
import com.community.volunteer.entity.ServiceRecord;
import com.community.volunteer.service.ActivityService;
import com.community.volunteer.service.ActivitySignupService;
import com.community.volunteer.service.ServiceRecordService;
import com.community.volunteer.service.VolunteerProfileService;
import com.community.volunteer.vo.statistics.StatisticSummaryVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final VolunteerProfileService volunteerProfileService;
    private final ActivityService activityService;
    private final ActivitySignupService activitySignupService;
    private final ServiceRecordService serviceRecordService;

    public StatisticsController(VolunteerProfileService volunteerProfileService,
                                ActivityService activityService,
                                ActivitySignupService activitySignupService,
                                ServiceRecordService serviceRecordService) {
        this.volunteerProfileService = volunteerProfileService;
        this.activityService = activityService;
        this.activitySignupService = activitySignupService;
        this.serviceRecordService = serviceRecordService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/summary")
    public ApiResponse<StatisticSummaryVO> summary() {
        long volunteerCount = volunteerProfileService.count();
        long activityCount = activityService.count();
        long signupCount = activitySignupService.count();
        double serviceHours = serviceRecordService.list().stream()
                .mapToDouble(ServiceRecord::getServiceHours)
                .sum();
        return ApiResponse.success(new StatisticSummaryVO(volunteerCount, activityCount, signupCount, serviceHours));
    }
}
