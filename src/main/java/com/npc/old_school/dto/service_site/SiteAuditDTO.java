package com.npc.old_school.dto.service_site;

import com.npc.old_school.entity.ServiceSiteEntity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SiteAuditDTO {
    @NotNull(message = "服务站点ID不能为空")
    private Long serviceSiteId;

    @NotBlank(message = "审核意见不能为空")
    private String auditRemark;

    @NotNull(message = "审核结果不能为空")
    private ServiceSiteEntity.AuditStatus auditStatus;
}
