package com.npc.old_school.dto.reservation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@SuppressWarnings("unused")
@Data
public class ReservationDTO {
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    @NotBlank(message = "学员姓名不能为空")
    private String studentName;
    
    @NotBlank(message = "身份证号不能为空")
    // TODO：生产环境需要开启
    // @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$", 
    //         message = "身份证号格式不正确")
    private String idCard;
    
    @NotBlank(message = "手机号不能为空")
    // TODO：生产环境需要开启
    // @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phoneNumber;
} 