package com.npc.old_school.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.npc.old_school.dto.course.CourseQueryDTO;
import com.npc.old_school.dto.service_site.ServiceSiteDTO;
import com.npc.old_school.dto.service_site.ServiceSiteQueryDTO;
import com.npc.old_school.dto.service_site.SiteAuditDTO;
import com.npc.old_school.dto.service_site.SiteCourseReportDTO;
import com.npc.old_school.dto.service_site.SiteReportDTO;
import com.npc.old_school.entity.ServiceSiteEntity;
import com.npc.old_school.exception.ResultResponse;
import com.npc.old_school.service.ServiceSiteService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/service-sites")
public class ServiceSiteController {

    @Autowired
    private ServiceSiteService serviceSiteService;

    /*
     * 创建服务站点
     */
    @PostMapping
    public ResultResponse createServiceSite(@Valid @RequestPart("data") ServiceSiteDTO dto,
            @RequestPart(value = "qrCode", required = false) MultipartFile qrCode,
            @RequestPart("images") List<MultipartFile> images) throws IOException {

        ServiceSiteEntity serviceSite = serviceSiteService.createServiceSite(dto, qrCode, images);
        return ResultResponse.success(serviceSite);

    }

    /*
     * 查询服务站点
     */
    @GetMapping
    public ResultResponse queryServiceSites(@Valid ServiceSiteQueryDTO queryDTO) {
        IPage<ServiceSiteEntity> page = serviceSiteService.queryServiceSites(queryDTO);
        return ResultResponse.success(page);
    }

    /*
     * 删除服务站点
     */
    @DeleteMapping("/{id}")
    public ResultResponse deleteServiceSite(@PathVariable Long id) {
        serviceSiteService.deleteServiceSite(id);
        return ResultResponse.success();
    }

    /*
     * 更新服务站点
     */
    @PutMapping("/{id}")
    public ResultResponse updateServiceSite(@PathVariable Long id,
            @Valid @RequestPart("data") ServiceSiteDTO dto,
            @RequestPart(value = "qrCode", required = false) MultipartFile qrCode,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages,
            @RequestParam(value = "deletedImageIds", required = false) List<Long> deletedImageIds) throws IOException {

        ServiceSiteEntity serviceSite = serviceSiteService.updateServiceSite(id, dto, qrCode, newImages,
                deletedImageIds);
        return ResultResponse.success(serviceSite);

    }

    /*
     * 获取服务站点详情
     */
    @GetMapping("/{id}")
    public ResultResponse getServiceSiteDetail(@PathVariable Long id) {
        ServiceSiteEntity serviceSite = serviceSiteService.getServiceSiteDetail(id);
        return ResultResponse.success(serviceSite);
    }

    /*
     * 审核服务站点
     */
    @PostMapping("/audit")
    public ResultResponse auditServiceSite(@Valid @RequestBody SiteAuditDTO dto) {
        ServiceSiteEntity serviceSite = serviceSiteService.auditServiceSite(dto);
        return ResultResponse.success(serviceSite);
    }

    @GetMapping("/report/export")
    public void exportSiteReport(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("站点报表", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        
        EasyExcel.write(response.getOutputStream(), SiteReportDTO.class)
            .sheet("站点报表")
            .doWrite(serviceSiteService.exportSiteReport());
    }

    @GetMapping("/{siteId}/courses/report/export")
    public void exportSiteCourseReport(
            @PathVariable Long siteId,
            HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("站点课程报表", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        
        EasyExcel.write(response.getOutputStream(), SiteCourseReportDTO.class)
            .sheet("站点课程报表")
            .doWrite(serviceSiteService.exportSiteCourseReport(siteId));
    }

    @GetMapping("/{siteId}/courses")
    public ResultResponse querySiteCourses(
            @PathVariable Long siteId,
            @Valid CourseQueryDTO queryDTO) {
        return ResultResponse.success(serviceSiteService.querySiteCourses(siteId, queryDTO));
    }
}
