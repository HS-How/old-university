package com.npc.old_school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.npc.old_school.dto.reservation.*;
import com.npc.old_school.entity.CourseEntity;
import com.npc.old_school.entity.CourseEntity.CourseStatus;
import com.npc.old_school.entity.CourseReservationEntity;
import com.npc.old_school.exception.BusinessException;
import com.npc.old_school.mapper.CourseMapper;
import com.npc.old_school.mapper.CourseReservationMapper;
import com.npc.old_school.service.CourseReservationService;
import com.npc.old_school.util.ExcelUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseReservationServiceImpl implements CourseReservationService {

    @Autowired
    private CourseReservationMapper reservationMapper;
    @Autowired
    private CourseMapper courseMapper;

    @Override
    @Transactional
    public CourseReservationEntity createReservation(ReservationDTO reservationDTO) {
        // 1. 检查课程是否存在且可以预约
        CourseEntity course = courseMapper.selectById(reservationDTO.getCourseId());
        if (course == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "课程不存在");
        }
        if (course.getCourseStatus() != CourseStatus.PUBLISHED) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "课程未发布，不能预约");
        }

        // 2. 检查是否已经预约过
        LambdaQueryWrapper<CourseReservationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseReservationEntity::getCourseId, reservationDTO.getCourseId())
                .eq(CourseReservationEntity::getIdCard, reservationDTO.getIdCard());
        if (reservationMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "该身份证已预约过此课程");
        }

        // 3. 检查课程是否已满
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseReservationEntity::getCourseId, reservationDTO.getCourseId());
        long currentCount = reservationMapper.selectCount(wrapper);
        if (currentCount >= course.getMaxStudents()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "课程已满");
        }

        // 4. 创建预约
        CourseReservationEntity reservation = new CourseReservationEntity();
        BeanUtils.copyProperties(reservationDTO, reservation);
        reservationMapper.insert(reservation);

        return reservation;
    }

    @Override
    public void deleteReservation(Long id) {
        CourseReservationEntity reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "预约记录不存在");
        }
        reservationMapper.deleteById(id);
    }

    @Override
    public IPage<CourseReservationEntity> queryReservations(ReservationQueryDTO queryDTO) {
        LambdaQueryWrapper<CourseReservationEntity> wrapper = new LambdaQueryWrapper<>();
        
        if (queryDTO.getCourseId() != null) {
            wrapper.eq(CourseReservationEntity::getCourseId, queryDTO.getCourseId());
        }
        if (StringUtils.hasText(queryDTO.getStudentName())) {
            wrapper.like(CourseReservationEntity::getStudentName, queryDTO.getStudentName());
        }
        if (StringUtils.hasText(queryDTO.getIdCard())) {
            wrapper.eq(CourseReservationEntity::getIdCard, queryDTO.getIdCard());
        }
        
        Page<CourseReservationEntity> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return reservationMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void importReservations(Long courseId, MultipartFile file) throws IOException {
        List<ReservationImportDTO> importDTOs = ExcelUtil.readExcel(file, ReservationImportDTO.class);
        List<String> errorMessages = new ArrayList<>();

        for (int i = 0; i < importDTOs.size(); i++) {
            try {
                ReservationDTO reservationDTO = new ReservationDTO();
                reservationDTO.setCourseId(courseId);
                BeanUtils.copyProperties(importDTOs.get(i), reservationDTO);
                createReservation(reservationDTO);
            } catch (Exception e) {
                errorMessages.add(String.format("第%d行导入失败：%s", i + 2, e.getMessage()));
            }
        }

        if (!errorMessages.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, String.join("\n", errorMessages));
        }
    }

    @Override
    public List<ReservationImportDTO> exportReservations(Long courseId) {
        LambdaQueryWrapper<CourseReservationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseReservationEntity::getCourseId, courseId);
        
        List<CourseReservationEntity> reservations = reservationMapper.selectList(wrapper);
        
        return reservations.stream().map(reservation -> {
            ReservationImportDTO exportDTO = new ReservationImportDTO();
            BeanUtils.copyProperties(reservation, exportDTO);
            return exportDTO;
        }).collect(Collectors.toList());
    }
} 