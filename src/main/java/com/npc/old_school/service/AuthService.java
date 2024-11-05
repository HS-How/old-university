package com.npc.old_school.service;

import com.npc.old_school.dto.auth.LoginDTO;
import com.npc.old_school.entity.UserEntity;

public interface AuthService {
    UserEntity login(LoginDTO loginDTO);
} 