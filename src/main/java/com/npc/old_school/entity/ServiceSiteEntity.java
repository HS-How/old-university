package com.npc.old_school.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
@TableName("public.service_site")
public class ServiceSiteEntity {

    public enum AuditStatus {
        DRAFT(0), PENDING(1), APPROVED(2), REJECTED(3), ARCHIVED(4);

        @EnumValue
        private final int value;

        AuditStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    @TableId
    private Long id;
    private String siteName;
    private String district;
    private String address;
    private String contactPhone;
    private String qrCodePath;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String openingHours;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Boolean isDeleted;

    @TableField(exist = false)
    private List<SiteImageEntity> images;

    private AuditStatus auditStatus = AuditStatus.PENDING;
    private String auditRemark; // 审核意见
    private Long auditorId; // 审核人ID
    private String auditorName; // 审核人姓名
    private LocalDateTime auditTime; // 审核时间
    // private Long previousVersionId; // 上一个版本的ID（用于版本追踪）
}
