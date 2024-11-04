package com.npc.old_school.dto.attendance;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class AttendanceExportDTO {
    @ExcelProperty("学员姓名")
    private String studentName;
    
    @ExcelProperty("联系电话")
    private String phoneNumber;
    
    @ExcelProperty("身份证号")
    private String idCard;
    
    @ExcelProperty("签到状态")
    private String attendanceStatus;
    
    @ExcelProperty("操作人")
    private String operatorName;
    
    @ExcelProperty("操作时间")
    private String operateTime;
} 