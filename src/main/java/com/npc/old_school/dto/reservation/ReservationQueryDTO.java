package com.npc.old_school.dto.reservation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ReservationQueryDTO {
    private Long courseId;
    private String studentName;
    private String idCard;
    
    @Min(value = 1)
    private Integer pageNum = 1;
    
    @Min(value = 1)
    @Max(value = 100)
    private Integer pageSize = 10;
} 