package com.npc.old_school.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.npc.old_school.dto.course.CourseQueryDTO;
import com.npc.old_school.dto.service_site.SiteCourseReportDTO;
import com.npc.old_school.dto.service_site.SiteReportDTO;
import com.npc.old_school.dto.stats.CityStatsDTO;
import com.npc.old_school.dto.stats.SiteCapacityDTO;
import com.npc.old_school.entity.ServiceSiteEntity;
import org.apache.ibatis.annotations.Param;

public interface ServiceSiteMapper extends BaseMapper<ServiceSiteEntity> {
    List<SiteReportDTO> getSiteReport();
    List<SiteCourseReportDTO> getSiteCourseReport(Long siteId, CourseQueryDTO queryDTO);
    
    // 获取区域概览数据
    CityStatsDTO getDistrictOverview(@Param("district") String district, @Param("month") String month);
    
    // 获取实际学位供给量TOP10
    List<SiteCapacityDTO> getActualCapacityTop10(@Param("district") String district, @Param("month") String month);
    
    // 获取理论学位供给量TOP10
    List<SiteCapacityDTO> getTheoreticalCapacityTop10(@Param("district") String district, @Param("month") String month);
    
    // 获取课程相关统计数据
    Map<String, Integer> getCourseStats(@Param("district") String district, @Param("month") String month);
}
