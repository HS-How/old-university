package com.npc.old_school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.npc.old_school.dto.role.RoleDTO;
import com.npc.old_school.dto.role.RoleQueryDTO;
import com.npc.old_school.entity.RoleEntity;
import com.npc.old_school.mapper.RoleMapper;
import com.npc.old_school.service.RoleService;
import com.npc.old_school.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public IPage<RoleEntity> queryRoles(RoleQueryDTO queryDTO) {
        Page<RoleEntity> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return roleMapper.selectRolesWithQuery(page, queryDTO);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        // 检查是否有用户关联此角色
        if (roleMapper.existsUserWithRole(id)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "该角色下存在用户，无法删除");
        }
        roleMapper.deleteById(id);
    }

    @Override
    public List<RoleEntity> getAllRoles() {
        return roleMapper.selectAllValidRoles();
    }

    @Override
    @Transactional
    public RoleEntity createRole(RoleDTO roleDTO) {
        // 检查角色编码是否已存在
        if (roleMapper.exists(new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getCode, roleDTO.getCode()))) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "角色编码已存在");
        }
        RoleEntity role = new RoleEntity();
        BeanUtils.copyProperties(roleDTO, role);
        roleMapper.insert(role);
        return role;
    }

    @Override
    @Transactional
    public RoleEntity updateRole(Long id, RoleDTO roleDTO) {
        RoleEntity role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "角色不存在");
        }
        // 检查角色编码是否已被其他角色使用
        if (!role.getCode().equals(roleDTO.getCode()) &&
                roleMapper.exists(new LambdaQueryWrapper<RoleEntity>()
                        .eq(RoleEntity::getCode, roleDTO.getCode()))) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "角色编码已存在");
        }
        BeanUtils.copyProperties(roleDTO, role);
        role.setId(id);
        roleMapper.updateById(role);
        return role;
    }
}