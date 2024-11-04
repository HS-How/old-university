package com.npc.old_school.dto.service_site;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

@Data
public class SiteReportDTO {
    @ExcelProperty("站点名称")
    private String siteName;

    @ExcelProperty("行政区划")
    private String district;

    @ExcelProperty("课程总数")
    private Integer totalCourses;

    @ExcelProperty("总出勤次数")
    private Integer totalAttendances;

    @ExcelProperty("理论课时量")
    private Integer theoreticalCapacity;

    @ExcelProperty("实际课时量")
    private Integer actualCapacity;
} 