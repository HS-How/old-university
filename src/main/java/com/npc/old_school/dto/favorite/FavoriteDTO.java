package com.npc.old_school.dto.favorite;

import com.npc.old_school.entity.FavoriteEntity.FavoriteType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoriteDTO {
    @NotBlank(message = "用户名不能为空")
    private String userName;
    
    @NotNull(message = "目标ID不能为空")
    private Long targetId;
    
    @NotNull(message = "收藏类型不能为空")
    private FavoriteType type;
} 