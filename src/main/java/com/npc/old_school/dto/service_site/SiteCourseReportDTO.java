package com.npc.old_school.dto.service_site;

import java.math.BigDecimal;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

@Data
public class SiteCourseReportDTO {
    @ExcelProperty("大课名称")
    private String courseName;           
    
    @ExcelProperty("授课老师")
    private String teacherName;          
    
    @ExcelProperty("课程大类")
    private String courseType;           
    
    @ExcelProperty("小课名称")
    private String subCourseName;        
    
    @ExcelProperty("课程学时")
    private Integer courseHours;         
    
    @ExcelProperty("报名人数")
    private Integer enrollmentCount;     
    
    @ExcelProperty("理论学位供给量")
    private BigDecimal theoreticalCapacity;  
    
    @ExcelProperty("实际学位供给量")
    private BigDecimal actualCapacity;       
} 