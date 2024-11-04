package com.npc.old_school.dto.service_site;

import com.npc.old_school.entity.ServiceSiteEntity.AuditStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ServiceSiteQueryDTO {
    private String siteName;
    private String district;
    private AuditStatus auditStatus;

    @Min(value = 1)
    private Integer pageNum = 1;

    @Min(value = 1)
    @Max(value = 100)
    private Integer pageSize = 10;
}
