package com.community.volunteer.controller.volunteer;

import com.community.volunteer.common.ApiResponse;
import com.community.volunteer.dto.volunteer.VolunteerSaveRequest;
import com.community.volunteer.service.VolunteerProfileService;
import com.community.volunteer.vo.common.PageVO;
import com.community.volunteer.vo.volunteer.VolunteerVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/volunteers")
public class VolunteerController {

    private final VolunteerProfileService volunteerProfileService;

    public VolunteerController(VolunteerProfileService volunteerProfileService) {
        this.volunteerProfileService = volunteerProfileService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<PageVO<VolunteerVO>> page(@RequestParam(defaultValue = "1") long pageNum,
                                                 @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(volunteerProfileService.pageVolunteer(pageNum, pageSize));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ApiResponse<VolunteerVO> detail(@PathVariable Long id) {
        return ApiResponse.success(volunteerProfileService.getVolunteerById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody VolunteerSaveRequest request) {
        volunteerProfileService.saveVolunteer(request);
        return ApiResponse.success(null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody VolunteerSaveRequest request) {
        volunteerProfileService.updateVolunteer(id, request);
        return ApiResponse.success(null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        volunteerProfileService.deleteVolunteer(id);
        return ApiResponse.success(null);
    }
}
