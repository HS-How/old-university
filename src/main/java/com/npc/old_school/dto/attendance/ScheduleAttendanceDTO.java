package com.npc.old_school.dto.attendance;

import com.npc.old_school.entity.CourseAttendanceEntity.AttendanceStatus;
import lombok.Data;

@Data
public class ScheduleAttendanceDTO {
    private Long studentId;
    private String studentName;
    private String phoneNumber;
    private String idCard;
    private AttendanceStatus attendanceStatus;
} 