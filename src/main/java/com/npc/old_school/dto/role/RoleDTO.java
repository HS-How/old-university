package com.npc.old_school.dto.role;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class RoleDTO {
    @NotBlank(message = "角色编码不能为空")
    private String code;
    
    @NotBlank(message = "角色名称不能为空")
    private String name;
    
    @NotNull(message = "状态不能为空")
    private Boolean status;
    
    @NotNull(message = "排序不能为空")
    private Integer sort;
} 