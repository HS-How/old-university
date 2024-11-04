package com.npc.old_school.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("public.user")
public class UserEntity {
    @TableId
    private Long id;
    private String username;
    private String password;
    private String name;
    private String phone;
    private Long roleId;
    private String district;
    private Long serviceSiteId;
    private Boolean status;
    
    @TableField(exist = false)
    private RoleEntity role;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Boolean isDeleted;
} 