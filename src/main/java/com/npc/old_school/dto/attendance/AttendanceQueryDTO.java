package com.npc.old_school.dto.attendance;

import com.npc.old_school.entity.CourseAttendanceEntity.AttendanceStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AttendanceQueryDTO {
    private Long courseId;
    private Long scheduleId;
    private String studentName;
    private String phoneNumber;
    private String idCard;
    private AttendanceStatus attendanceStatus;

    @Min(value = 1)
    private Integer pageNum = 1;

    @Min(value = 1)
    @Max(value = 100)
    private Integer pageSize = 10;
}