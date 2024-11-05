package com.npc.old_school.dto.favorite;

import com.npc.old_school.entity.FavoriteEntity.FavoriteType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class FavoriteQueryDTO {
    private String userName;
    private FavoriteType type;
    
    @Min(value = 1)
    private Integer pageNum = 1;
    
    @Min(value = 1)
    @Max(value = 100)
    private Integer pageSize = 10;
} 