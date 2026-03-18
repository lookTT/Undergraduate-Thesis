package com.community.volunteer.controller.activity;

import com.community.volunteer.common.ApiResponse;
import com.community.volunteer.security.JwtPrincipal;
import com.community.volunteer.service.ActivitySignupService;
import com.community.volunteer.service.VolunteerProfileService;
import com.community.volunteer.vo.activity.ActivitySignupVO;
import com.community.volunteer.vo.common.PageVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActivitySignupController {

    private final ActivitySignupService activitySignupService;
    private final VolunteerProfileService volunteerProfileService;

    public ActivitySignupController(ActivitySignupService activitySignupService,
                                    VolunteerProfileService volunteerProfileService) {
        this.activitySignupService = activitySignupService;
        this.volunteerProfileService = volunteerProfileService;
    }

    @PreAuthorize("hasRole('VOLUNTEER')")
    @PostMapping("/api/activities/{id}/signup")
    public ApiResponse<Void> signup(@PathVariable("id") Long activityId,
                                    Authentication authentication) {
        Long userId = ((JwtPrincipal) authentication.getPrincipal()).userId();
        Long volunteerId = volunteerProfileService.findVolunteerIdByUserId(userId);
        activitySignupService.signup(activityId, volunteerId);
        return ApiResponse.success(null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/activities/{id}/signups")
    public ApiResponse<PageVO<ActivitySignupVO>> list(@PathVariable("id") Long activityId,
                                                      @RequestParam(defaultValue = "1") long pageNum,
                                                      @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(activitySignupService.pageByActivity(activityId, pageNum, pageSize));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/api/signups/{id}/approve")
    public ApiResponse<Void> approve(@PathVariable("id") Long signupId) {
        activitySignupService.approve(signupId);
        return ApiResponse.success(null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/api/signups/{id}/reject")
    public ApiResponse<Void> reject(@PathVariable("id") Long signupId) {
        activitySignupService.reject(signupId);
        return ApiResponse.success(null);
    }
}
