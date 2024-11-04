package com.npc.old_school.controller;

import com.npc.old_school.dto.role.RoleDTO;
import com.npc.old_school.dto.role.RoleQueryDTO;
import com.npc.old_school.service.RoleService;
import com.npc.old_school.exception.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/roles")
public class RoleController {
    @Autowired
    private RoleService roleService;
    
    @GetMapping
    public ResultResponse queryRoles(@Valid RoleQueryDTO queryDTO) {
        return ResultResponse.success(roleService.queryRoles(queryDTO));
    }
    
    @GetMapping("/all")
    public ResultResponse getAllRoles() {
        return ResultResponse.success(roleService.getAllRoles());
    }
    
    @PostMapping
    public ResultResponse createRole(@Valid @RequestBody RoleDTO roleDTO) {
        return ResultResponse.success(roleService.createRole(roleDTO));
    }
    
    @PutMapping("/{id}")
    public ResultResponse updateRole(@PathVariable Long id, @Valid @RequestBody RoleDTO roleDTO) {
        return ResultResponse.success(roleService.updateRole(id, roleDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResultResponse deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResultResponse.success();
    }
} 