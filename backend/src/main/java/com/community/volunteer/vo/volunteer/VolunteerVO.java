package com.community.volunteer.vo.volunteer;

public record VolunteerVO(
        Long id,
        Long userId,
        String studentOrMemberNo,
        String gender,
        Integer age,
        String communityName,
        String skillTag,
        String remark
) {
}
