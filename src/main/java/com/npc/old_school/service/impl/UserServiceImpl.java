package com.npc.old_school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.npc.old_school.entity.UserEntity;
import com.npc.old_school.dto.user.UserDTO;
import com.npc.old_school.dto.user.UserQueryDTO;
import com.npc.old_school.dto.user.UpdateUserDTO;
import com.npc.old_school.exception.BusinessException;
import com.npc.old_school.mapper.UserMapper;
import com.npc.old_school.mapper.RoleMapper;
import com.npc.old_school.entity.RoleEntity;
import com.npc.old_school.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public IPage<UserEntity> queryUsers(UserQueryDTO queryDTO) {
        Page<UserEntity> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return userMapper.selectUsersWithRole(page, queryDTO);
    }
    
    @Override
    @Transactional
    public UserEntity createUser(UserDTO userDTO) {
        // 验证密码
        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "两次输入的密码不一致");
        }
        
        // 检查用户名是否已存在
        if (userMapper.exists(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, userDTO.getUsername()))) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "用户名已存在");
        }
        
        // 验证角色是否存在
        RoleEntity role = roleMapper.selectById(userDTO.getRoleId());
        if (role == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "角色不存在");
        }
        
        UserEntity user = new UserEntity();
        BeanUtils.copyProperties(userDTO, user);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        
        userMapper.insert(user);
        return userMapper.selectUserWithRole(user.getId());
    }
    
    @Override
    @Transactional
    public UserEntity updateUser(Long id, UpdateUserDTO userDTO) {
        UserEntity user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        
        // 如果修改了密码
        if (StringUtils.hasText(userDTO.getPassword())) {
            // 如果提供了密码，则确认密码也必须提供
            if (!StringUtils.hasText(userDTO.getConfirmPassword())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "请输入确认密码");
            }
            // 验证两次密码是否一致
            if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "两次输入的密码不一致");
            }
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        // 验证角色是否存在
        RoleEntity role = roleMapper.selectById(userDTO.getRoleId());
        if (role == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "角色不存在");
        }
        
        // 检查用户名是否被其他用户使用
        if (!user.getUsername().equals(userDTO.getUsername())) {
            if (userMapper.exists(new LambdaQueryWrapper<UserEntity>()
                    .eq(UserEntity::getUsername, userDTO.getUsername())
                    .ne(UserEntity::getId, id))) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "用户名已存在");
            }
        }
        
        BeanUtils.copyProperties(userDTO, user, "id", "password");
        userMapper.updateById(user);
        return userMapper.selectUserWithRole(id);
    }
    
    @Override
    @Transactional
    public void deleteUsers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "请选择要删除的用户");
        }
        userMapper.deleteBatchIds(ids);
    }
} 