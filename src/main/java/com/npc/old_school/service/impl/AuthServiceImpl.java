package com.npc.old_school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.npc.old_school.dto.auth.LoginDTO;
import com.npc.old_school.entity.UserEntity;
import com.npc.old_school.exception.BusinessException;
import com.npc.old_school.mapper.UserMapper;
import com.npc.old_school.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserEntity login(LoginDTO loginDTO) {
        UserEntity user = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
            .eq(UserEntity::getUsername, loginDTO.getUsername()));
            
        if (user == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "用户名或密码错误");
        }
        
        // 使用 passwordEncoder.matches 来验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "用户名或密码错误");
        }

        user.setPassword(null);
        return user;
    }
}
