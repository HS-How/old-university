package com.npc.old_school.controller;

import com.alibaba.excel.EasyExcel;
import com.npc.old_school.dto.course.CourseDTO;
import com.npc.old_school.dto.course.CourseImportDTO;
import com.npc.old_school.dto.course.CourseQueryDTO;
import com.npc.old_school.exception.ResultResponse;
import com.npc.old_school.service.CourseService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    @Autowired
    private CourseService courseService;

    @PostMapping
    public ResultResponse createCourse(
            @Valid @RequestPart("course") CourseDTO courseDTO,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage) throws IOException {
        return ResultResponse.success(courseService.createCourse(courseDTO, coverImage));
    }

    @PutMapping("/{id}")
    public ResultResponse updateCourse(
            @PathVariable Long id,
            @Valid @RequestPart("course") CourseDTO courseDTO,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage) throws IOException {
        return ResultResponse.success(courseService.updateCourse(id, courseDTO, coverImage));
    }

    @DeleteMapping("/{id}")
    public ResultResponse deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResultResponse.success();
    }

    @GetMapping
    public ResultResponse queryCourses(@Valid CourseQueryDTO queryDTO) {
        return ResultResponse.success(courseService.queryCourses(queryDTO));
    }

    @GetMapping("/{id}")
    public ResultResponse getCourseDetail(@PathVariable Long id) {
        return ResultResponse.success(courseService.getCourseDetail(id));
    }

    @PostMapping("/{id}/publish")
    public ResultResponse publishCourse(@PathVariable Long id) {
        return ResultResponse.success(courseService.publishCourse(id));
    }

    @PostMapping("/{id}/offline")
    public ResultResponse offlineCourse(@PathVariable Long id) {
        return ResultResponse.success(courseService.offlineCourse(id));
    }

    @PostMapping("/{id}/copy")
    public ResultResponse copyCourse(@PathVariable Long id) {
        return ResultResponse.success(courseService.copyCourse(id));
    }

    @PostMapping("/import")
    public ResultResponse importCourses(@RequestParam("file") MultipartFile file) throws IOException {
        courseService.importCourses(file);
        return ResultResponse.success();
    }

    @GetMapping("/export")
    public void exportCourses(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("课程信息", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        
        EasyExcel.write(response.getOutputStream(), CourseImportDTO.class)
            .sheet("课程信息")
            .doWrite(courseService.exportCourses());
    }

    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("课程导入模板", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        
        EasyExcel.write(response.getOutputStream(), CourseImportDTO.class)
            .sheet("课程导入模板")
            .doWrite(Collections.emptyList());
    }
} 