package com.npc.old_school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.npc.old_school.entity.UserEntity;
import com.npc.old_school.dto.user.UserQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
    UserEntity selectUserWithRole(@Param("id") Long id);
    IPage<UserEntity> selectUsersWithRole(Page<UserEntity> page, @Param("query") UserQueryDTO query);
} 