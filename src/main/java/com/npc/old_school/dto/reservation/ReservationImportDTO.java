package com.npc.old_school.dto.reservation;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ReservationImportDTO {
    @ExcelProperty("学员姓名")
    private String studentName;
    
    @ExcelProperty("身份证号")
    private String idCard;
    
    @ExcelProperty("手机号码")
    private String phoneNumber;
} 