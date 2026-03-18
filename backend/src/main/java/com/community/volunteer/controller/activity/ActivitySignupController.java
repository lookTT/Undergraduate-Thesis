package com.community.volunteer.controller.activity;

import com.community.volunteer.common.ApiResponse;
import com.community.volunteer.security.JwtTokenService;
import com.community.volunteer.service.ActivitySignupService;
import com.community.volunteer.service.VolunteerProfileService;
import com.community.volunteer.vo.activity.ActivitySignupVO;
import com.community.volunteer.vo.common.PageVO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActivitySignupController {

    private final ActivitySignupService activitySignupService;
    private final JwtTokenService jwtTokenService;
    private final VolunteerProfileService volunteerProfileService;

    public ActivitySignupController(ActivitySignupService activitySignupService,
                                    JwtTokenService jwtTokenService,
                                    VolunteerProfileService volunteerProfileService) {
        this.activitySignupService = activitySignupService;
        this.jwtTokenService = jwtTokenService;
        this.volunteerProfileService = volunteerProfileService;
    }

    @PostMapping("/api/activities/{id}/signup")
    public ApiResponse<Void> signup(@PathVariable("id") Long activityId,
                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        Long userId = jwtTokenService.getUserId(authorization);
        Long volunteerId = volunteerProfileService.findVolunteerIdByUserId(userId);
        activitySignupService.signup(activityId, volunteerId);
        return ApiResponse.success(null);
    }

    @GetMapping("/api/activities/{id}/signups")
    public ApiResponse<PageVO<ActivitySignupVO>> list(@PathVariable("id") Long activityId,
                                                      @RequestParam(defaultValue = "1") long pageNum,
                                                      @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(activitySignupService.pageByActivity(activityId, pageNum, pageSize));
    }

    @PutMapping("/api/signups/{id}/approve")
    public ApiResponse<Void> approve(@PathVariable("id") Long signupId) {
        activitySignupService.approve(signupId);
        return ApiResponse.success(null);
    }

    @PutMapping("/api/signups/{id}/reject")
    public ApiResponse<Void> reject(@PathVariable("id") Long signupId) {
        activitySignupService.reject(signupId);
        return ApiResponse.success(null);
    }
}
