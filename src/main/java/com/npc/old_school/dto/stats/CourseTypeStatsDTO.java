package com.npc.old_school.dto.stats;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CourseTypeStatsDTO {
    private String courseType;           // 课程大类
    private Integer count;               // 数量
    private BigDecimal percentage;       // 占比
} 