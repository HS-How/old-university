package com.npc.old_school.dto.stats;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class SiteCapacityDTO {
    private String siteName;             // 站点名称
    private Integer rank;                // 排名
    private BigDecimal capacity;         // 学位供给量
} 