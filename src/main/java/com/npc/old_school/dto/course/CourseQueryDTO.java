package com.npc.old_school.dto.course;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CourseQueryDTO {
    private String name;
    private Long serviceSiteId;
    private Long teacherId;
    private String courseType;
    private String semester;
    private Boolean isCharged;
    private String courseStatus;
    
    @Min(value = 1)
    private Integer pageNum = 1;
    
    @Min(value = 1)
    @Max(value = 100)
    private Integer pageSize = 10;
} 