package com.npc.old_school.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.npc.old_school.dto.reservation.ReservationDTO;
import com.npc.old_school.dto.reservation.ReservationQueryDTO;
import com.npc.old_school.dto.reservation.ReservationImportDTO;
import com.npc.old_school.entity.CourseReservationEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CourseReservationService {
    CourseReservationEntity createReservation(ReservationDTO reservationDTO);
    void deleteReservation(Long id);
    IPage<CourseReservationEntity> queryReservations(ReservationQueryDTO queryDTO);
    void importReservations(Long courseId, MultipartFile file) throws IOException;
    List<ReservationImportDTO> exportReservations(Long courseId);
} 