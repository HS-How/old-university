package com.npc.old_school.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.npc.old_school.dto.teacher.TeacherDTO;
import com.npc.old_school.dto.teacher.TeacherQueryDTO;
import com.npc.old_school.entity.TeacherEntity;
import com.npc.old_school.exception.BusinessException;
import com.npc.old_school.exception.ResultResponse;
import com.npc.old_school.service.TeacherService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/teachers")
@Slf4j
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @PostMapping
    public ResultResponse addTeacher(@Valid @RequestPart("data") TeacherDTO teacherDTO,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws IOException {
        try {
            TeacherEntity teacher = teacherService.addTeacher(teacherDTO, photo);
            return ResultResponse.success(teacher);
        } catch (IOException e) {
            log.error("创建教师失败", e);
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "创建教师失败");
        }
    }

    @DeleteMapping("/{id}")
    public ResultResponse deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResultResponse.success();
    }

    @PutMapping("/{id}")
    public ResultResponse updateTeacher(@PathVariable Long id, @Valid @RequestPart("data") TeacherDTO teacherDTO,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws IOException {
        try {
            TeacherEntity teacher = teacherService.updateTeacher(id, teacherDTO, photo);
            return ResultResponse.success(teacher);
        } catch (IOException e) {
            log.error("更新教师失败", e);
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "更新教师失败");
        }
    }

    @GetMapping
    public ResultResponse queryTeachers(@Valid TeacherQueryDTO queryDTO) {
        IPage<TeacherEntity> page = teacherService.queryTeachers(queryDTO);
        return ResultResponse.success(page);
    }
}
