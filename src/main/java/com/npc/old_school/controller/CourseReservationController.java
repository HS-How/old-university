package com.npc.old_school.controller;

import com.alibaba.excel.EasyExcel;
import com.npc.old_school.dto.reservation.ReservationDTO;
import com.npc.old_school.dto.reservation.ReservationImportDTO;
import com.npc.old_school.dto.reservation.ReservationQueryDTO;
import com.npc.old_school.exception.ResultResponse;
import com.npc.old_school.service.CourseReservationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;

@RestController
@RequestMapping("/api/course-reservations")
@RequiredArgsConstructor
public class CourseReservationController {
    @Autowired
    private CourseReservationService reservationService;

    @PostMapping
    public ResultResponse createReservation(@Valid @RequestBody ReservationDTO reservationDTO) {
        return ResultResponse.success(reservationService.createReservation(reservationDTO));
    }

    @DeleteMapping("/{id}")
    public ResultResponse deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResultResponse.success();
    }

    @GetMapping
    public ResultResponse queryReservations(@Valid ReservationQueryDTO queryDTO) {
        return ResultResponse.success(reservationService.queryReservations(queryDTO));
    }

    @PostMapping("/import/{courseId}")
    public ResultResponse importReservations(
            @PathVariable Long courseId,
            @RequestParam("file") MultipartFile file) throws IOException {
        reservationService.importReservations(courseId, file);
        return ResultResponse.success();
    }

    @GetMapping("/export/{courseId}")
    public void exportReservations(
            @PathVariable Long courseId,
            HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("预约名单", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        
        EasyExcel.write(response.getOutputStream(), ReservationImportDTO.class)
            .sheet("预约名单")
            .doWrite(reservationService.exportReservations(courseId));
    }

    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("预约导入模板", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        
        EasyExcel.write(response.getOutputStream(), ReservationImportDTO.class)
            .sheet("预约导入模板")
            .doWrite(Collections.emptyList());
    }
} 