package com.npc.old_school.dto.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UserQueryDTO {
    private String username;
    private String name;
    private Long serviceSiteId;
    private String phone;

    @Min(value = 1)
    private Integer pageNum = 1;

    @Min(value = 1)
    @Max(value = 100)
    private Integer pageSize = 10;
} 