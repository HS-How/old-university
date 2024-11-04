package com.npc.old_school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.npc.old_school.dto.stats.TeachingHoursDTO;
import com.npc.old_school.entity.TeacherEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeacherMapper extends BaseMapper<TeacherEntity> {
    // 获取教师授课学时统计
    List<TeachingHoursDTO> getTeachingHours(@Param("district") String district, @Param("month") String month);
}
