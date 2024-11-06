package com.npc.old_school.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.npc.old_school.dto.attendance.AttendanceDTO;
import com.npc.old_school.dto.attendance.AttendanceQueryDTO;
import com.npc.old_school.dto.attendance.ScheduleAttendanceDTO;
import com.npc.old_school.dto.attendance.ScheduleAttendanceQueryDTO;
import com.npc.old_school.entity.CourseAttendanceEntity;
import java.util.List;

public interface CourseAttendanceService {
    void takeAttendance(AttendanceDTO attendanceDTO);
    IPage<CourseAttendanceEntity> queryAttendance(AttendanceQueryDTO queryDTO);
    List<CourseAttendanceEntity> exportAttendance(Long courseId, Long scheduleId);
    IPage<ScheduleAttendanceDTO> getScheduleAttendance(ScheduleAttendanceQueryDTO queryDTO);
} 