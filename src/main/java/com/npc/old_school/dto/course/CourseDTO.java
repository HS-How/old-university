package com.npc.old_school.dto.course;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CourseDTO {
    @NotBlank(message = "课程名称不能为空")
    private String name;
    
    @NotNull(message = "教师ID不能为空")
    private Long teacherId;
    
    @NotNull(message = "服务站点ID不能为空")
    private Long serviceSiteId;
    
    @NotBlank(message = "课程类型不能为空")
    private String courseType;
    
    private String courseSubtype;
    
    private Boolean isCharged = false;
    
    @DecimalMin(value = "0.0", message = "课程费用不能为负数")
    private BigDecimal fee;
    
    @NotNull(message = "年份不能为空")
    private Integer year;
    
    @NotBlank(message = "学期不能为空")
    private String semester;
    
    @NotNull(message = "报名开始日期不能为空")
    private LocalDate enrollmentStartDate;
    
    @NotNull(message = "报名结束日期不能为空")
    private LocalDate enrollmentEndDate;
    
    @NotNull(message = "课程开始日期不能为空")
    private LocalDate courseStartDate;
    
    @NotNull(message = "课程结束日期不能为空")
    private LocalDate courseEndDate;
    
    @Min(value = 1, message = "最大报名人数必须大于0")
    private Integer maxStudents;
    
    @NotBlank(message = "上课地点不能为空")
    private String location;
    
    private String description;
    
    private List<CourseScheduleDTO> schedules;
} 