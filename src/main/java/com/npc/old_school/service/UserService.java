package com.npc.old_school.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.npc.old_school.dto.user.UserDTO;
import com.npc.old_school.dto.user.UserQueryDTO;
import com.npc.old_school.dto.user.UpdateUserDTO;
import com.npc.old_school.entity.UserEntity;

public interface UserService {
    IPage<UserEntity> queryUsers(UserQueryDTO queryDTO);
    UserEntity createUser(UserDTO userDTO);
    UserEntity updateUser(Long id, UpdateUserDTO userDTO);
    void deleteUsers(List<Long> ids);
} 