package com.npc.old_school.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.npc.old_school.dto.course.CourseDTO;
import com.npc.old_school.dto.course.CourseImportDTO;
import com.npc.old_school.dto.course.CourseQueryDTO;
import com.npc.old_school.entity.CourseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface CourseService {
    CourseEntity createCourse(CourseDTO courseDTO, MultipartFile coverImage) throws IOException;
    CourseEntity updateCourse(Long id, CourseDTO courseDTO, MultipartFile coverImage) throws IOException;
    void deleteCourse(Long id);
    IPage<CourseEntity> queryCourses(CourseQueryDTO queryDTO);
    CourseEntity getCourseDetail(Long id);
    CourseEntity publishCourse(Long id);
    CourseEntity offlineCourse(Long id);
    CourseEntity copyCourse(Long id);
    void importCourses(MultipartFile file) throws IOException;
    List<CourseImportDTO> exportCourses();
} 