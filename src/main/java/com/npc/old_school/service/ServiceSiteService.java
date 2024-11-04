package com.npc.old_school.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.npc.old_school.dto.course.CourseQueryDTO;
import com.npc.old_school.dto.service_site.ServiceSiteDTO;
import com.npc.old_school.dto.service_site.ServiceSiteQueryDTO;
import com.npc.old_school.dto.service_site.SiteAuditDTO;
import com.npc.old_school.dto.service_site.SiteCourseReportDTO;
import com.npc.old_school.dto.service_site.SiteReportDTO;
import com.npc.old_school.entity.ServiceSiteEntity;
import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ServiceSiteService {
    ServiceSiteEntity createServiceSite(ServiceSiteDTO dto, MultipartFile qrCode, List<MultipartFile> images)
            throws IOException;

    IPage<ServiceSiteEntity> queryServiceSites(ServiceSiteQueryDTO queryDTO);

    void deleteServiceSite(Long id);

    ServiceSiteEntity updateServiceSite(Long id, ServiceSiteDTO dto, MultipartFile qrCode,
            List<MultipartFile> newImages, List<Long> deleteImageIds) throws IOException;

    ServiceSiteEntity getServiceSiteDetail(Long id);

    ServiceSiteEntity auditServiceSite(SiteAuditDTO dto);

    IPage<ServiceSiteEntity> queryAuditServiceSites(ServiceSiteEntity.AuditStatus auditStatus);

    List<SiteReportDTO> exportSiteReport();

    List<SiteCourseReportDTO> exportSiteCourseReport(Long siteId);

    IPage<SiteCourseReportDTO> querySiteCourses(Long siteId, CourseQueryDTO queryDTO);
}
