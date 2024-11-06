package com.npc.old_school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.npc.old_school.dto.attendance.AttendanceDTO;
import com.npc.old_school.dto.attendance.AttendanceQueryDTO;
import com.npc.old_school.dto.attendance.ScheduleAttendanceDTO;
import com.npc.old_school.dto.attendance.ScheduleAttendanceQueryDTO;
import com.npc.old_school.entity.CourseAttendanceEntity;
import com.npc.old_school.entity.CourseAttendanceEntity.AttendanceStatus;
import com.npc.old_school.entity.CourseReservationEntity;
import com.npc.old_school.exception.BusinessException;
import com.npc.old_school.mapper.CourseAttendanceMapper;
import com.npc.old_school.mapper.CourseReservationMapper;
import com.npc.old_school.service.CourseAttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseAttendanceServiceImpl implements CourseAttendanceService {

    @Autowired
    private CourseAttendanceMapper attendanceMapper;
    @Autowired
    private CourseReservationMapper courseReservationMapper;

    @Override
    @Transactional
    public void takeAttendance(AttendanceDTO attendanceDTO) {
        // TODO: 获取当前操作用户信息
        Long operatorId = 1L;
        String operatorName = "测试用户";

        for (Long studentId : attendanceDTO.getStudentIds()) {
            // 首先检查学生是否有该课程的预约记录
            if (!hasReservation(attendanceDTO.getCourseId(), studentId)) {
                throw new BusinessException("学生未预约该课程,无法进行考勤");
            }

            // 检查是否已经签到
            LambdaQueryWrapper<CourseAttendanceEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CourseAttendanceEntity::getCourseId, attendanceDTO.getCourseId())
                    .eq(CourseAttendanceEntity::getScheduleId, attendanceDTO.getScheduleId())
                    .eq(CourseAttendanceEntity::getStudentId, studentId);

            CourseAttendanceEntity attendance = attendanceMapper.selectOne(wrapper);

            if (attendance == null) {
                // 创建新的签到记录
                attendance = new CourseAttendanceEntity();
                attendance.setCourseId(attendanceDTO.getCourseId());
                attendance.setScheduleId(attendanceDTO.getScheduleId());
                attendance.setStudentId(studentId);
                attendance.setAttendanceStatus(AttendanceStatus.PRESENT);
                attendance.setOperatorId(operatorId);
                attendance.setOperatorName(operatorName);
                attendanceMapper.insert(attendance);
            } else {
                // 更新签到状态
                attendance.setAttendanceStatus(AttendanceStatus.PRESENT);
                attendance.setOperatorId(operatorId);
                attendance.setOperatorName(operatorName);
                attendanceMapper.updateById(attendance);
            }
        }
    }

    @Override
    public IPage<CourseAttendanceEntity> queryAttendance(AttendanceQueryDTO queryDTO) {
        Page<CourseAttendanceEntity> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        // 设置按ID升序排序
        page.addOrder(OrderItem.ascs("id"));
        return attendanceMapper.queryAttendanceWithDetails(page, queryDTO);
    }

    @Override
    public List<CourseAttendanceEntity> exportAttendance(Long courseId, Long scheduleId) {
        return attendanceMapper.queryAttendanceWithDetailsBySchedule(courseId, scheduleId);
    }

    @Override
    public IPage<ScheduleAttendanceDTO> getScheduleAttendance(ScheduleAttendanceQueryDTO queryDTO) {
        // 1. 创建分页对象
        Page<CourseReservationEntity> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 2. 获取该课程的所有预约学员（分页）
        LambdaQueryWrapper<CourseReservationEntity> reservationWrapper = new LambdaQueryWrapper<>();
        reservationWrapper.eq(CourseReservationEntity::getCourseId, queryDTO.getCourseId());
        Page<CourseReservationEntity> reservationPage = courseReservationMapper.selectPage(page, reservationWrapper);

        // 3. 获取当前页学生的签到记录
        List<Long> currentPageStudentIds = reservationPage.getRecords().stream()
                .map(CourseReservationEntity::getId)
                .collect(Collectors.toList());
        
        LambdaQueryWrapper<CourseAttendanceEntity> attendanceWrapper = new LambdaQueryWrapper<>();
        attendanceWrapper.eq(CourseAttendanceEntity::getCourseId, queryDTO.getCourseId())
                .eq(CourseAttendanceEntity::getScheduleId, queryDTO.getScheduleId())
                .in(CourseAttendanceEntity::getStudentId, currentPageStudentIds);
        List<CourseAttendanceEntity> attendances = attendanceMapper.selectList(attendanceWrapper);

        // 4. 将签到记录转换为Map
        Map<Long, CourseAttendanceEntity> attendanceMap = attendances.stream()
                .collect(Collectors.toMap(
                        CourseAttendanceEntity::getStudentId,
                        attendance -> attendance
                ));

        // 5. 构建返回结果
        List<ScheduleAttendanceDTO> dtoList = reservationPage.getRecords().stream().map(reservation -> {
            ScheduleAttendanceDTO dto = new ScheduleAttendanceDTO();
            dto.setStudentId(reservation.getId());
            dto.setStudentName(reservation.getStudentName());
            dto.setPhoneNumber(reservation.getPhoneNumber());
            dto.setIdCard(reservation.getIdCard());

            CourseAttendanceEntity attendance = attendanceMap.get(reservation.getId());
            dto.setAttendanceStatus(attendance != null ? attendance.getAttendanceStatus() : AttendanceStatus.ABSENT);

            return dto;
        }).collect(Collectors.toList());

        // 6. 封装分页结果
        Page<ScheduleAttendanceDTO> resultPage = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize(), reservationPage.getTotal());
        resultPage.setRecords(dtoList);
        
        return resultPage;
    }

    private boolean hasReservation(Long courseId, Long studentId) {
        // 实现检查学生是否有课程预约的逻辑
        // 需要注入 CourseReservationMapper 并查询预约记录
        return courseReservationMapper.exists(
                new LambdaQueryWrapper<CourseReservationEntity>()
                        .eq(CourseReservationEntity::getCourseId, courseId)
                        .eq(CourseReservationEntity::getId, studentId));
    }
}