package com.npc.old_school.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("public.teacher")
public class TeacherEntity {

    public enum CourseType {
        SPORTS("体育"),
        CALLIGRAPHY("书法"),
        THEORY("理论"),
        EDUCATION("教育"),
        MUSIC("音乐"),
        DANCE("舞蹈");

        @EnumValue
        private final String description;

        CourseType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public static List<Map<String, String>> getAllTypes() {
            return Arrays.stream(CourseType.values())
                    .map(type -> {
                        Map<String, String> map = new HashMap<>();
                        map.put("value", type.name());
                        map.put("label", type.getDescription());
                        return map;
                    })
                    .collect(Collectors.toList());
        }
    }

    @TableId
    private Long id;
    private String name;
    private String idCard;
    private String contactPhone;
    private Integer age;
    private String gender;
    private String introduction;
    private String photoPath;
    private String courseTypes; // 存储课程类型，用逗号分隔

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Boolean isDeleted;

    @TableField(exist = false)
    private List<String> courseTypeList; // 用于前端展示的课程类型列表
    
    public void setCourseTypeList(List<String> courseTypeList) {
        this.courseTypeList = courseTypeList;
        this.courseTypes = String.join(",", courseTypeList);
    }
    
    public List<String> getCourseTypeList() {
        if (courseTypes == null || courseTypes.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(courseTypes.split(","));
    }
}
