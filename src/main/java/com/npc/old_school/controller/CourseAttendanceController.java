package com.npc.old_school.controller;

import com.alibaba.excel.EasyExcel;
import com.npc.old_school.dto.attendance.AttendanceDTO;
import com.npc.old_school.dto.attendance.AttendanceExportDTO;
import com.npc.old_school.dto.attendance.AttendanceQueryDTO;
import com.npc.old_school.dto.attendance.ScheduleAttendanceQueryDTO;
import com.npc.old_school.entity.CourseAttendanceEntity;
import com.npc.old_school.exception.ResultResponse;
import com.npc.old_school.service.CourseAttendanceService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class CourseAttendanceController {
    @Autowired
    private CourseAttendanceService attendanceService;

    @PostMapping("/take")
    public ResultResponse takeAttendance(@Valid @RequestBody AttendanceDTO attendanceDTO) {
        attendanceService.takeAttendance(attendanceDTO);
        return ResultResponse.success();
    }

    @GetMapping
    public ResultResponse queryAttendance(@Valid AttendanceQueryDTO queryDTO) {
        return ResultResponse.success(attendanceService.queryAttendance(queryDTO));
    }

    @GetMapping("/export")
    public void exportAttendance(
            @RequestParam Long courseId,
            @RequestParam Long scheduleId,
            HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("签到表", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        // 获取签到数据
        List<CourseAttendanceEntity> attendanceList = attendanceService.exportAttendance(courseId, scheduleId);

        // 转换为导出DTO
        List<AttendanceExportDTO> exportList = new ArrayList<>();
        for (CourseAttendanceEntity attendance : attendanceList) {
            AttendanceExportDTO exportDTO = new AttendanceExportDTO();
            exportDTO.setStudentName(attendance.getStudent().getStudentName());
            exportDTO.setPhoneNumber(attendance.getStudent().getPhoneNumber());
            exportDTO.setIdCard(attendance.getStudent().getIdCard());
            exportDTO.setAttendanceStatus(attendance.getAttendanceStatus().getDescription());
            exportDTO.setOperatorName(attendance.getOperatorName());
            exportDTO.setOperateTime(attendance.getUpdatedAt().toString());
            exportList.add(exportDTO);
        }

        EasyExcel.write(response.getOutputStream(), AttendanceExportDTO.class)
                .sheet("签到表")
                .doWrite(exportList);
    }

    @GetMapping("/schedule")
    public ResultResponse getScheduleAttendance(@Valid ScheduleAttendanceQueryDTO queryDTO) {
        return ResultResponse.success(attendanceService.getScheduleAttendance(queryDTO));
    }
} 