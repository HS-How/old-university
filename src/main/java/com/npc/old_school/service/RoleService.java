package com.npc.old_school.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.npc.old_school.dto.role.RoleDTO;
import com.npc.old_school.dto.role.RoleQueryDTO;
import com.npc.old_school.entity.RoleEntity;

public interface RoleService {
    IPage<RoleEntity> queryRoles(RoleQueryDTO queryDTO);
    RoleEntity createRole(RoleDTO roleDTO);
    RoleEntity updateRole(Long id, RoleDTO roleDTO);
    void deleteRole(Long id);
    List<RoleEntity> getAllRoles(); // 用于下拉选择
} 