package com.community.volunteer.dto.volunteer;

import jakarta.validation.constraints.NotBlank;

public record VolunteerSaveRequest(
        Long userId,
        @NotBlank(message = "学号/成员编号不能为空")
        String studentOrMemberNo,
        String gender,
        Integer age,
        String communityName,
        String skillTag,
        String remark
) {
}
