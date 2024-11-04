package com.npc.old_school.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("public.course_reservation")
public class CourseReservationEntity {
    @TableId
    private Long id;
    private Long courseId;
    private String studentName;
    private String idCard;
    private String phoneNumber;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Boolean isDeleted;
    
    @TableField(exist = false)
    private CourseEntity course;
} 