package com.npc.old_school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.npc.old_school.dto.course.CourseDTO;
import com.npc.old_school.dto.course.CourseQueryDTO;
import com.npc.old_school.dto.course.CourseScheduleDTO;
import com.npc.old_school.entity.CourseEntity;
import com.npc.old_school.entity.CourseEntity.CourseStatus;
import com.npc.old_school.entity.CourseScheduleEntity;
import com.npc.old_school.entity.TeacherEntity;
import com.npc.old_school.entity.ServiceSiteEntity;
import com.npc.old_school.exception.BusinessException;
import com.npc.old_school.mapper.CourseMapper;
import com.npc.old_school.mapper.CourseScheduleMapper;
import com.npc.old_school.mapper.TeacherMapper;
import com.npc.old_school.mapper.ServiceSiteMapper;
import com.npc.old_school.service.CourseService;
import com.npc.old_school.util.FileUtil;
import com.npc.old_school.util.ExcelUtil;
import com.npc.old_school.dto.course.CourseImportDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseScheduleMapper scheduleMapper;
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private ServiceSiteMapper serviceSiteMapper;

    @Autowired
    private FileUtil fileUtil;

    @Override
    @Transactional
    public CourseEntity createCourse(CourseDTO courseDTO, MultipartFile coverImage) throws IOException {
        // 1. 验证基本数据
        validateCourseData(courseDTO);

        // 2. 创建课程实体
        CourseEntity course = new CourseEntity();
        BeanUtils.copyProperties(courseDTO, course);

        // 3. 处理封面图片
        if (coverImage != null && !coverImage.isEmpty()) {
            String coverPath = fileUtil.saveFile(coverImage, "course-covers");
            course.setCoverImage(coverPath);
        }

        // 4. 计算总课时
        int totalHours = courseDTO.getSchedules().stream()
                .mapToInt(CourseScheduleDTO::getHours)
                .sum();
        course.setTotalHours(totalHours);

        // 5. 设置初始状态
        course.setCourseStatus(CourseStatus.DRAFT);

        // 6. 保存课程
        courseMapper.insert(course);

        // 7. 保存课程时间表
        saveSchedules(course.getId(), courseDTO.getSchedules());

        return getCourseDetail(course.getId());
    }

    @Override
    @Transactional
    public CourseEntity updateCourse(Long id, CourseDTO courseDTO, MultipartFile coverImage) throws IOException {
        // 1. 检查课程是否存在
        CourseEntity course = courseMapper.selectById(id);
        if (course == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "课程不存在");
        }

        // 2. 验证基本数据
        validateCourseData(courseDTO);

        // 3. 保存原有的封面路径
        String originalCoverPath = course.getCoverImage();

        // 4. 复制属性
        BeanUtils.copyProperties(courseDTO, course, "id", "status", "coverImage");

        // 5. 处理新的封面图片
        if (coverImage != null && !coverImage.isEmpty()) {
            fileUtil.deleteFile(originalCoverPath);
            String coverPath = fileUtil.saveFile(coverImage, "course-covers");
            course.setCoverImage(coverPath);
        }

        // 6. 更新总课时
        int totalHours = courseDTO.getSchedules().stream()
                .mapToInt(CourseScheduleDTO::getHours)
                .sum();
        course.setTotalHours(totalHours);

        // 7. 更新课程
        courseMapper.updateById(course);

        // 8. 更新课程时间表
        // 先删除原有的时间表
        LambdaQueryWrapper<CourseScheduleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseScheduleEntity::getCourseId, id);
        scheduleMapper.delete(wrapper);
        // 保存新的时间表
        saveSchedules(id, courseDTO.getSchedules());

        return getCourseDetail(id);
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        CourseEntity course = courseMapper.selectById(id);
        if (course == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "课程不存在");
        }

        // 删除课程（逻辑删除）
        courseMapper.deleteById(id);

        // 删除相关的时间表（逻辑删除）
        LambdaQueryWrapper<CourseScheduleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseScheduleEntity::getCourseId, id);
        scheduleMapper.delete(wrapper);
    }

    @Override
    public IPage<CourseEntity> queryCourses(CourseQueryDTO queryDTO) {
        Page<CourseEntity> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<CourseEntity> coursePage = courseMapper.queryCoursesWithDetails(page, queryDTO);

        // 填充教师和服务站点信息
        coursePage.getRecords().forEach(course -> {
            TeacherEntity teacher = teacherMapper.selectById(course.getTeacherId());
            ServiceSiteEntity site = serviceSiteMapper.selectById(course.getServiceSiteId());
            course.setTeacher(teacher);
            course.setServiceSite(site);
        });

        return coursePage;
    }

    @Override
    public CourseEntity getCourseDetail(Long id) {
        CourseEntity course = courseMapper.selectById(id);
        if (course == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "课程不存在");
        }

        // 获取课程时间表
        LambdaQueryWrapper<CourseScheduleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseScheduleEntity::getCourseId, id);
        List<CourseScheduleEntity> schedules = scheduleMapper.selectList(wrapper);
        course.setSchedules(schedules);

        // 获取教师信息
        TeacherEntity teacher = teacherMapper.selectById(course.getTeacherId());
        course.setTeacher(teacher);

        // 获取服务站点信息
        ServiceSiteEntity site = serviceSiteMapper.selectById(course.getServiceSiteId());
        course.setServiceSite(site);

        return course;
    }

    @Override
    @Transactional
    public CourseEntity publishCourse(Long id) {
        CourseEntity course = courseMapper.selectById(id);
        if (course == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "课程不存在");
        }

        course.setCourseStatus(CourseStatus.PUBLISHED);
        courseMapper.updateById(course);

        return getCourseDetail(id);
    }

    @Override
    @Transactional
    public CourseEntity offlineCourse(Long id) {
        CourseEntity course = courseMapper.selectById(id);
        if (course == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "课程不存在");
        }

        course.setCourseStatus(CourseStatus.OFFLINE);
        courseMapper.updateById(course);

        return getCourseDetail(id);
    }

    @Override
    @Transactional
    public CourseEntity copyCourse(Long id) {
        // 1. 获取原课程信息
        CourseEntity sourceCourse = getCourseDetail(id);

        // 2. 创建新课程
        CourseEntity newCourse = new CourseEntity();
        BeanUtils.copyProperties(sourceCourse, newCourse,
                "id", "status", "createdAt", "updatedAt");
        newCourse.setName(sourceCourse.getName() + " - 副本");
        newCourse.setCourseStatus(CourseStatus.DRAFT);

        // 3. 保存新课程
        courseMapper.insert(newCourse);

        // 4. 复制课程时间表
        sourceCourse.getSchedules().forEach(schedule -> {
            CourseScheduleEntity newSchedule = new CourseScheduleEntity();
            BeanUtils.copyProperties(schedule, newSchedule,
                    "id", "courseId", "createdAt", "updatedAt");
            newSchedule.setCourseId(newCourse.getId());
            scheduleMapper.insert(newSchedule);
        });

        return getCourseDetail(newCourse.getId());
    }

    private void validateCourseData(CourseDTO courseDTO) {
        // 验证日期逻辑
        if (courseDTO.getEnrollmentEndDate().isBefore(courseDTO.getEnrollmentStartDate())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "报名结束日期不能早于开始日期");
        }
        if (courseDTO.getCourseEndDate().isBefore(courseDTO.getCourseStartDate())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "课程结束日期不能早于开始日期");
        }
        if (courseDTO.getCourseStartDate().isBefore(courseDTO.getEnrollmentEndDate())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "课程开始日期必须晚于报名结束日期");
        }

        // 验证课程时间表
        if (courseDTO.getSchedules() == null || courseDTO.getSchedules().isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "课程时间表不能为空");
        }
    }

    private void saveSchedules(Long courseId, List<CourseScheduleDTO> schedules) {
        schedules.forEach(scheduleDTO -> {
            CourseScheduleEntity schedule = new CourseScheduleEntity();
            BeanUtils.copyProperties(scheduleDTO, schedule);
            schedule.setCourseId(courseId);
            scheduleMapper.insert(schedule);
        });
    }

    @Override
    @Transactional
    public void importCourses(MultipartFile file) throws IOException {
        List<CourseImportDTO> importDTOs = ExcelUtil.readExcel(file, CourseImportDTO.class);
        List<String> errorMessages = new ArrayList<>();

        for (int i = 0; i < importDTOs.size(); i++) {
            try {
                CourseImportDTO importDTO = importDTOs.get(i);
                CourseDTO courseDTO = convertImportDTO(importDTO);
                createCourse(courseDTO, null);
            } catch (Exception e) {
                errorMessages.add(String.format("第%d行导入失败：%s", i + 2, e.getMessage()));
            }
        }

        if (!errorMessages.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, String.join("\n", errorMessages));
        }
    }

    private CourseDTO convertImportDTO(CourseImportDTO importDTO) {
        CourseDTO courseDTO = new CourseDTO();

        // 基本信息转换
        courseDTO.setName(importDTO.getName());

        // 查找教师
        TeacherEntity teacher = teacherMapper.selectOne(
                new LambdaQueryWrapper<TeacherEntity>()
                        .eq(TeacherEntity::getName, importDTO.getTeacherName()));
        if (teacher == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    String.format("教师[%s]不存在", importDTO.getTeacherName()));
        }
        courseDTO.setTeacherId(teacher.getId());

        // 查找服务站点
        ServiceSiteEntity site = serviceSiteMapper.selectOne(
                new LambdaQueryWrapper<ServiceSiteEntity>()
                        .eq(ServiceSiteEntity::getSiteName, importDTO.getServiceSiteName()));
        if (site == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    String.format("服务站点[%s]不存在", importDTO.getServiceSiteName()));
        }
        courseDTO.setServiceSiteId(site.getId());

        // 其他字段转换
        courseDTO.setCourseType(importDTO.getCourseType());
        courseDTO.setCourseSubtype(importDTO.getCourseSubtype());
        courseDTO.setIsCharged("是".equals(importDTO.getIsCharged()));
        courseDTO.setFee(importDTO.getFee() != null ? new BigDecimal(importDTO.getFee()) : null);
        courseDTO.setYear(Integer.parseInt(importDTO.getYear()));
        courseDTO.setSemester(importDTO.getSemester());

        // 日期转换
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        courseDTO.setEnrollmentStartDate(LocalDate.parse(importDTO.getEnrollmentStartDate(), formatter));
        courseDTO.setEnrollmentEndDate(LocalDate.parse(importDTO.getEnrollmentEndDate(), formatter));
        courseDTO.setCourseStartDate(LocalDate.parse(importDTO.getCourseStartDate(), formatter));
        courseDTO.setCourseEndDate(LocalDate.parse(importDTO.getCourseEndDate(), formatter));

        courseDTO.setMaxStudents(Integer.parseInt(importDTO.getMaxStudents()));
        courseDTO.setLocation(importDTO.getLocation());
        courseDTO.setDescription(importDTO.getDescription());

        return courseDTO;
    }

    @Override
    public List<CourseImportDTO> exportCourses() {
        // 查询所有课程
        List<CourseEntity> courses = courseMapper.selectList(null);

        // 转换为导出格式
        return courses.stream().map(course -> {
            CourseImportDTO exportDTO = new CourseImportDTO();

            exportDTO.setName(course.getName());

            // 获取教师信息
            TeacherEntity teacher = teacherMapper.selectById(course.getTeacherId());
            exportDTO.setTeacherName(teacher != null ? teacher.getName() : "");

            // 获取服务站点信息
            ServiceSiteEntity site = serviceSiteMapper.selectById(course.getServiceSiteId());
            exportDTO.setServiceSiteName(site != null ? site.getSiteName() : "");

            // 设置其他字段
            exportDTO.setCourseType(course.getCourseType());
            exportDTO.setCourseSubtype(course.getCourseSubtype());
            exportDTO.setIsCharged(course.getIsCharged() ? "是" : "否");
            exportDTO.setFee(course.getFee() != null ? course.getFee().toString() : "");
            exportDTO.setYear(course.getYear().toString());
            exportDTO.setSemester(course.getSemester());

            // 格式化日期
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            exportDTO.setEnrollmentStartDate(course.getEnrollmentStartDate().format(formatter));
            exportDTO.setEnrollmentEndDate(course.getEnrollmentEndDate().format(formatter));
            exportDTO.setCourseStartDate(course.getCourseStartDate().format(formatter));
            exportDTO.setCourseEndDate(course.getCourseEndDate().format(formatter));

            exportDTO.setMaxStudents(course.getMaxStudents().toString());
            exportDTO.setLocation(course.getLocation());
            exportDTO.setDescription(course.getDescription());

            return exportDTO;
        }).collect(Collectors.toList());
    }
}