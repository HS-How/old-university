package com.npc.old_school.dto.role;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
public class RoleQueryDTO {
    private String keyword; // 用于搜索code或name

    @Min(value = 1)
    private Integer pageNum = 1;

    @Min(value = 1)
    @Max(value = 100)
    private Integer pageSize = 10;
} 