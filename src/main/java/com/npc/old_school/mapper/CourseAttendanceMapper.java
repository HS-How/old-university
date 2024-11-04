package com.npc.old_school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.npc.old_school.dto.attendance.AttendanceQueryDTO;
import com.npc.old_school.entity.CourseAttendanceEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseAttendanceMapper extends BaseMapper<CourseAttendanceEntity> {
    IPage<CourseAttendanceEntity> queryAttendanceWithDetails(
            Page<CourseAttendanceEntity> page,
            @Param("query") AttendanceQueryDTO query);

    List<CourseAttendanceEntity> queryAttendanceWithDetailsBySchedule(
            @Param("courseId") Long courseId,
            @Param("scheduleId") Long scheduleId);
}