package com.npc.old_school.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.npc.old_school.dto.teacher.TeacherDTO;
import com.npc.old_school.dto.teacher.TeacherQueryDTO;
import com.npc.old_school.entity.TeacherEntity;
import com.npc.old_school.entity.TeacherEntity.CourseType;
import com.npc.old_school.exception.BusinessException;
import com.npc.old_school.mapper.TeacherMapper;
import com.npc.old_school.service.TeacherService;
import com.npc.old_school.util.FileUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    public TeacherEntity addTeacher(TeacherDTO teacherDTO, MultipartFile photo) throws IOException {
        // 1. 验证课程类型
        validateCourseTypes(teacherDTO.getCourseTypes());

        // 先检查身份证号是否已存在
        LambdaQueryWrapper<TeacherEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherEntity::getIdCard, teacherDTO.getIdCard());
        if (teacherMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "该身份证号已存在");
        }

        TeacherEntity teacherEntity = new TeacherEntity();
        BeanUtils.copyProperties(teacherDTO, teacherEntity);

        // 2. 设置课程类型
        teacherEntity.setCourseTypeList(teacherDTO.getCourseTypes());

        // 3. 处理上传照片
        if (photo != null && !photo.isEmpty()) {
            String photoPath = fileUtil.saveFile(photo, "teacher-photoes");
            teacherEntity.setPhotoPath(photoPath);
        }

        teacherMapper.insert(teacherEntity);

        return teacherEntity;
    }

    @Override
    public void deleteTeacher(Long id) {
        TeacherEntity teacherEntity = teacherMapper.selectById(id);
        if(teacherEntity==null){
            throw new BusinessException(HttpStatus.NOT_FOUND,"教师不存在");
        }

        // 删除教师图片
        fileUtil.deleteFile(teacherEntity.getPhotoPath());

        teacherMapper.deleteById(id);
    }

    @Override
    public IPage<TeacherEntity> queryTeachers(TeacherQueryDTO queryDTO) {
        LambdaQueryWrapper<TeacherEntity> wrapper = new LambdaQueryWrapper<>();
        if(StringUtils.hasText(queryDTO.getName())){
            wrapper.like(TeacherEntity::getName, queryDTO.getName());
        }
        if(StringUtils.hasText(queryDTO.getCourseType())){
            wrapper.like(TeacherEntity::getCourseTypes, queryDTO.getCourseType());
        }
        if(StringUtils.hasText(queryDTO.getGender())){
            wrapper.eq(TeacherEntity::getGender, queryDTO.getGender());
        }
        Page<TeacherEntity> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return teacherMapper.selectPage(page, wrapper);
    }

    @Override
    public TeacherEntity updateTeacher(Long id, TeacherDTO teacherDTO, MultipartFile photo) throws IOException {
        // 1. 验证课程类型
        validateCourseTypes(teacherDTO.getCourseTypes());

        TeacherEntity teacherEntity = teacherMapper.selectById(id);
        if(teacherEntity==null){
            throw new BusinessException(HttpStatus.NOT_FOUND,"教师不存在");
        }   

        // 保存原有的照片路径
        String originalPhotoPath = teacherEntity.getPhotoPath();
        
        // 复制属性，但需要排除id字段
        BeanUtils.copyProperties(teacherDTO, teacherEntity, "id");
        
        // 确保ID不变
        teacherEntity.setId(id);
        
        // 2. 设置课程类型
        teacherEntity.setCourseTypeList(teacherDTO.getCourseTypes());

        // 3. 处理上传照片
        if (photo != null && !photo.isEmpty()) {
            // 删除旧照片
            fileUtil.deleteFile(originalPhotoPath);
            String photoPath = fileUtil.saveFile(photo, "teacher-photoes");
            teacherEntity.setPhotoPath(photoPath);
        } else {
            // 如果没有新照片，保留原来的照片路径
            teacherEntity.setPhotoPath(originalPhotoPath);
        }

        teacherMapper.updateById(teacherEntity);

        return teacherEntity;
    }

    private void validateCourseTypes(List<String> courseTypes) {
        if (courseTypes == null || courseTypes.isEmpty()) {
            throw new BusinessException("课程类型不能为空");
        }

        Set<String> validTypes = Arrays.stream(CourseType.values())
                .map(CourseType::getDescription)
                .collect(Collectors.toSet());

        for (String type : courseTypes) {
            if (!validTypes.contains(type)) {
                throw new BusinessException("无效的课程类型: " + type);
            }
        }
    }

}
