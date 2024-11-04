package com.npc.old_school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.npc.old_school.dto.course.CourseQueryDTO;
import com.npc.old_school.dto.stats.CourseTypeStatsDTO;
import com.npc.old_school.entity.CourseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseMapper extends BaseMapper<CourseEntity> {
    IPage<CourseEntity> queryCoursesWithDetails(Page<CourseEntity> page, @Param("query") CourseQueryDTO query);

    // 获取课程类型统计
    List<CourseTypeStatsDTO> getCourseTypeStats(@Param("district") String district, @Param("month") String month);
} 