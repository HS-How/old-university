package com.npc.old_school.dto.course;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class CourseImportDTO {
    @ExcelProperty("课程名称")
    private String name;
    
    @ExcelProperty("教师姓名")
    private String teacherName;
    
    @ExcelProperty("服务站点")
    private String serviceSiteName;
    
    @ExcelProperty("课程大类")
    private String courseType;
    
    @ExcelProperty("课程小类")
    private String courseSubtype;
    
    @ExcelProperty("是否收费")
    private String isCharged;
    
    @ExcelProperty("课程费用")
    private String fee;
    
    @ExcelProperty("年份")
    private String year;
    
    @ExcelProperty("学期")
    private String semester;
    
    @ExcelProperty("报名开始日期")
    private String enrollmentStartDate;
    
    @ExcelProperty("报名结束日期")
    private String enrollmentEndDate;
    
    @ExcelProperty("课程开始日期")
    private String courseStartDate;
    
    @ExcelProperty("课程结束日期")
    private String courseEndDate;
    
    @ExcelProperty("最大报名人数")
    private String maxStudents;
    
    @ExcelProperty("上课地点")
    private String location;
    
    @ExcelProperty("课程简介")
    private String description;
} 