package com.npc.old_school.dto.teacher;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class TeacherQueryDTO {
    private String name;
    private String courseType; // 单个课程类型
    private String gender;

    @Min(value = 1)
    private Integer pageNum = 1;

    @Min(value = 1)
    @Max(value = 100)
    private Integer pageSize = 10;
}