package com.npc.old_school.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("public.favorite")
public class FavoriteEntity {
    
    public enum FavoriteType {
        COURSE("COURSE"),
        SERVICE_SITE("SERVICE_SITE");
        
        @EnumValue
        private final String description;
        
        FavoriteType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    @TableId
    private Long id;
    private String userName;
    private Long targetId;
    private FavoriteType type;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Boolean isDeleted;
    
    @TableField(exist = false)
    private CourseEntity course;
    
    @TableField(exist = false)
    private ServiceSiteEntity serviceSite;
} 