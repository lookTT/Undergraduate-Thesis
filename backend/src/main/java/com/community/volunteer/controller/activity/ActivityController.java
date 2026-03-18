package com.community.volunteer.controller.activity;

import com.community.volunteer.common.ApiResponse;
import com.community.volunteer.dto.activity.ActivitySaveRequest;
import com.community.volunteer.service.ActivityService;
import com.community.volunteer.security.JwtPrincipal;
import com.community.volunteer.vo.activity.ActivityVO;
import com.community.volunteer.vo.common.PageVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/api/activities")
    public ApiResponse<PageVO<ActivityVO>> page(@RequestParam(defaultValue = "1") long pageNum,
                                                @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(activityService.pageActivity(pageNum, pageSize));
    }

    @GetMapping("/api/activities/{id}")
    public ApiResponse<ActivityVO> detail(@PathVariable Long id) {
        return ApiResponse.success(activityService.getActivityById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/activities")
    public ApiResponse<Void> create(@Valid @RequestBody ActivitySaveRequest request,
                                    Authentication authentication) {
        Long creatorId = ((JwtPrincipal) authentication.getPrincipal()).userId();
        activityService.saveActivity(request, creatorId);
        return ApiResponse.success(null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/api/activities/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody ActivitySaveRequest request) {
        activityService.updateActivity(id, request);
        return ApiResponse.success(null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/api/activities/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ApiResponse.success(null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/api/activities/{id}/publish")
    public ApiResponse<Void> publish(@PathVariable Long id) {
        activityService.publishActivity(id);
        return ApiResponse.success(null);
    }
}
