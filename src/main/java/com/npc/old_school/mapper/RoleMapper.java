package com.npc.old_school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.npc.old_school.entity.RoleEntity;
import com.npc.old_school.dto.role.RoleQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<RoleEntity> {
    IPage<RoleEntity> selectRolesWithQuery(Page<RoleEntity> page, @Param("query") RoleQueryDTO query);
    List<RoleEntity> selectAllValidRoles();
    boolean existsUserWithRole(@Param("roleId") Long roleId);
} 