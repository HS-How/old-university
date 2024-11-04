package com.npc.old_school.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("public.course_schedule")
public class CourseScheduleEntity {
    @TableId
    private Long id;
    private Long courseId;
    private String subCourseName;
    private Integer hours;
    private LocalDateTime classTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Boolean isDeleted;
}
