package com.community.volunteer.controller.activity;

import com.community.volunteer.common.ApiResponse;
import com.community.volunteer.dto.activity.ActivitySaveRequest;
import com.community.volunteer.service.AuthService;
import com.community.volunteer.service.ActivityService;
import com.community.volunteer.vo.activity.ActivityVO;
import com.community.volunteer.vo.common.PageVO;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;
    private final AuthService authService;

    public ActivityController(ActivityService activityService, AuthService authService) {
        this.activityService = activityService;
        this.authService = authService;
    }

    @GetMapping
    public ApiResponse<PageVO<ActivityVO>> page(@RequestParam(defaultValue = "1") long pageNum,
                                                @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(activityService.pageActivity(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<ActivityVO> detail(@PathVariable Long id) {
        return ApiResponse.success(activityService.getActivityById(id));
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody ActivitySaveRequest request,
                                    @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        Long creatorId = authService.currentUser(authorization).id();
        activityService.saveActivity(request, creatorId);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody ActivitySaveRequest request) {
        activityService.updateActivity(id, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/publish")
    public ApiResponse<Void> publish(@PathVariable Long id) {
        activityService.publishActivity(id);
        return ApiResponse.success(null);
    }
}
