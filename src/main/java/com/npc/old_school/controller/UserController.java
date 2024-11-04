package com.npc.old_school.controller;

import com.npc.old_school.dto.user.UserDTO;
import com.npc.old_school.dto.user.UserQueryDTO;
import com.npc.old_school.dto.user.UpdateUserDTO;
import com.npc.old_school.exception.ResultResponse;
import com.npc.old_school.service.RoleService;
import com.npc.old_school.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;
    
    @GetMapping
    public ResultResponse queryUsers(@Valid UserQueryDTO queryDTO) {
        return ResultResponse.success(userService.queryUsers(queryDTO));
    }
    
    @PostMapping
    public ResultResponse createUser(@Valid @RequestBody UserDTO userDTO) {
        return ResultResponse.success(userService.createUser(userDTO));
    }
    
    @PutMapping("/{id}")
    public ResultResponse updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserDTO userDTO) {
        return ResultResponse.success(userService.updateUser(id, userDTO));
    }
    
    @DeleteMapping
    public ResultResponse deleteUsers(@RequestParam("ids") List<Long> ids) {
        userService.deleteUsers(ids);
        return ResultResponse.success();
    }
    
    @GetMapping("/roles")
    public ResultResponse getRoles() {
        return ResultResponse.success(roleService.getAllRoles());
    }
} 