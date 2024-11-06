package com.npc.old_school.dto.attendance;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScheduleAttendanceQueryDTO {
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    @NotNull(message = "小课ID不能为空")
    private Long scheduleId;

    @Min(value = 1)
    private Integer pageNum = 1;

    @Min(value = 1)
    @Max(value = 100)
    private Integer pageSize = 10;
}
