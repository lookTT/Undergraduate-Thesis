package com.community.volunteer.controller.activity;

import com.community.volunteer.common.ApiResponse;
import com.community.volunteer.security.JwtTokenService;
import com.community.volunteer.service.ActivityCheckinService;
import com.community.volunteer.service.VolunteerProfileService;
import com.community.volunteer.vo.activity.ActivityCheckinVO;
import com.community.volunteer.vo.common.PageVO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActivityCheckinController {

    private final ActivityCheckinService activityCheckinService;
    private final JwtTokenService jwtTokenService;
    private final VolunteerProfileService volunteerProfileService;

    public ActivityCheckinController(ActivityCheckinService activityCheckinService,
                                     JwtTokenService jwtTokenService,
                                     VolunteerProfileService volunteerProfileService) {
        this.activityCheckinService = activityCheckinService;
        this.jwtTokenService = jwtTokenService;
        this.volunteerProfileService = volunteerProfileService;
    }

    @PostMapping("/api/activities/{id}/checkin")
    public ApiResponse<Void> checkin(@PathVariable("id") Long activityId,
                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        Long userId = jwtTokenService.getUserId(authorization);
        Long volunteerId = volunteerProfileService.findVolunteerIdByUserId(userId);
        activityCheckinService.checkin(activityId, volunteerId);
        return ApiResponse.success(null);
    }

    @GetMapping("/api/activities/{id}/checkins")
    public ApiResponse<PageVO<ActivityCheckinVO>> list(@PathVariable("id") Long activityId,
                                                       @RequestParam(defaultValue = "1") long pageNum,
                                                       @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(activityCheckinService.pageByActivity(activityId, pageNum, pageSize));
    }
}
