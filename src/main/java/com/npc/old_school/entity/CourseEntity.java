package com.npc.old_school.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("public.course")
public class CourseEntity {

    public enum CourseStatus {
        DRAFT("DRAFT"), PUBLISHED("PUBLISHED"), OFFLINE("OFFLINE");

        @EnumValue
        private final String description;

        CourseStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    @TableId
    private Long id;
    private String name;
    private Long teacherId;
    private Long serviceSiteId;
    private String courseType;
    private String courseSubtype;
    private Boolean isCharged;
    private BigDecimal fee;
    private Integer year;
    private String semester;
    private LocalDate enrollmentStartDate;
    private LocalDate enrollmentEndDate;
    private LocalDate courseStartDate;
    private LocalDate courseEndDate;
    private Integer maxStudents;
    private String location;
    private String description;
    private String coverImage;
    private CourseStatus courseStatus;
    private Integer totalHours;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Boolean isDeleted;

    @TableField(exist = false)
    private TeacherEntity teacher;

    @TableField(exist = false)
    private ServiceSiteEntity serviceSite;

    @TableField(exist = false)
    private List<CourseScheduleEntity> schedules;
}
