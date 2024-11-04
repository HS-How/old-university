package com.npc.old_school.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("public.course_attendance")
public class CourseAttendanceEntity {
    
    public enum AttendanceStatus {
        PRESENT("已到"),
        ABSENT("未到"),
        LEAVE("请假");

        @EnumValue
        private final String description;

        AttendanceStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    @TableId
    private Long id;
    private Long courseId;
    private Long scheduleId;
    private Long studentId;
    private AttendanceStatus attendanceStatus;
    private Long operatorId;
    private String operatorName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Boolean isDeleted;

    @TableField(exist = false)
    private CourseReservationEntity student;
    
    @TableField(exist = false)
    private CourseScheduleEntity schedule;
} 