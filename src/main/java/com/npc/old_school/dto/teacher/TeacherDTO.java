package com.npc.old_school.dto.teacher;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class TeacherDTO {
    private Long id;
    @NotBlank(message = "姓名不能为空")
    private String name;
    @NotBlank(message = "身份证不能为空")
    private String idCard;
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;
    private Integer age;
    private String gender;
    private String introduction;
    @NotEmpty(message = "课程类型不能为空")
    private List<String> courseTypes;
}