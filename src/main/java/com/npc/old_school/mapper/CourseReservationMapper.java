package com.npc.old_school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.npc.old_school.dto.stats.StudentAgeStatsDTO;
import com.npc.old_school.entity.CourseReservationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CourseReservationMapper extends BaseMapper<CourseReservationEntity> {
    // 获取学员年龄统计
    List<StudentAgeStatsDTO> getStudentAgeStats(@Param("district") String district, @Param("month") String month);
    
    // 获取学员性别统计
    Map<String, Object> getStudentGenderStats(@Param("district") String district, @Param("month") String month);
} 