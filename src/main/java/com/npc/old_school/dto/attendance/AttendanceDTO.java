package com.npc.old_school.dto.attendance;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class AttendanceDTO {
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    @NotNull(message = "课程时间表ID不能为空")
    private Long scheduleId;
    
    private List<Long> studentIds;
} 