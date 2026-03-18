package com.community.volunteer.controller.activity;

import com.community.volunteer.common.ApiResponse;
import com.community.volunteer.security.JwtPrincipal;
import com.community.volunteer.service.ActivityCheckinService;
import com.community.volunteer.service.VolunteerProfileService;
import com.community.volunteer.vo.activity.ActivityCheckinVO;
import com.community.volunteer.vo.common.PageVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActivityCheckinController {

    private final ActivityCheckinService activityCheckinService;
    private final VolunteerProfileService volunteerProfileService;

    public ActivityCheckinController(ActivityCheckinService activityCheckinService,
                                     VolunteerProfileService volunteerProfileService) {
        this.activityCheckinService = activityCheckinService;
        this.volunteerProfileService = volunteerProfileService;
    }

    @PreAuthorize("hasRole('VOLUNTEER')")
    @PostMapping("/api/activities/{id}/checkin")
    public ApiResponse<Void> checkin(@PathVariable("id") Long activityId,
                                     Authentication authentication) {
        Long userId = ((JwtPrincipal) authentication.getPrincipal()).userId();
        Long volunteerId = volunteerProfileService.findVolunteerIdByUserId(userId);
        activityCheckinService.checkin(activityId, volunteerId);
        return ApiResponse.success(null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/activities/{id}/checkins")
    public ApiResponse<PageVO<ActivityCheckinVO>> list(@PathVariable("id") Long activityId,
                                                       @RequestParam(defaultValue = "1") long pageNum,
                                                       @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(activityCheckinService.pageByActivity(activityId, pageNum, pageSize));
    }
}
