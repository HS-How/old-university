package com.npc.old_school.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.npc.old_school.dto.teacher.TeacherDTO;
import com.npc.old_school.dto.teacher.TeacherQueryDTO;
import com.npc.old_school.entity.TeacherEntity;

public interface TeacherService {
    TeacherEntity addTeacher(TeacherDTO teacherDTO, MultipartFile photo) throws IOException;
    void deleteTeacher(Long id);
    IPage<TeacherEntity> queryTeachers(TeacherQueryDTO queryDTO);
    TeacherEntity updateTeacher(Long id, TeacherDTO teacherDTO, MultipartFile photo) throws IOException;
}
