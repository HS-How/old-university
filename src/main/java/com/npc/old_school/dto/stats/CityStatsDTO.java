package com.npc.old_school.dto.stats;

import lombok.Data;

@Data
public class CityStatsDTO {
    private Integer siteCount;           // 站点数量
    private Integer courseCount;         // 课程数量
    private Integer teacherCount;        // 师资数量
    private Integer totalEnrollments;    // 累计报名次数
    private Integer totalAttendances;    // 累计签到次数
} 