package com.npc.old_school.dto.service_site;

import lombok.Data;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ServiceSiteDTO {
    @NotBlank(message = "站点名称不能为空")
    private String siteName;

    @NotBlank(message = "行政区划不能为空")
    private String district;

    @NotBlank(message = "地址不能为空")
    private String address;

    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    @NotBlank(message = "营业时间不能为空")
    private String openingHours;

    @NotNull(message = "纬度不能为空")
    private BigDecimal latitude;

    @NotNull(message = "经度不能为空")
    private BigDecimal longitude;
}
