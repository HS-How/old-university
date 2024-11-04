package com.npc.old_school.dto.course;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CourseScheduleDTO {
    @NotBlank(message = "小课名称不能为空")
    private String subCourseName;
    
    @NotNull(message = "课时不能为空")
    @Min(value = 1, message = "课时必须大于0")
    private Integer hours;
    
    @NotNull(message = "上课时间不能为空")
    private LocalDateTime classTime;
} 