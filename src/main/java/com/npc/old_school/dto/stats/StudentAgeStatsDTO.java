package com.npc.old_school.dto.stats;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class StudentAgeStatsDTO {
    private String ageRange;             // 年龄段
    private Integer count;               // 数量
    private BigDecimal percentage;       // 占比
} 