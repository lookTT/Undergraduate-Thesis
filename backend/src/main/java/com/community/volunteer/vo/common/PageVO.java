package com.community.volunteer.vo.common;

import java.util.List;

public record PageVO<T>(
        long pageNum,
        long pageSize,
        long total,
        List<T> records
) {
}
