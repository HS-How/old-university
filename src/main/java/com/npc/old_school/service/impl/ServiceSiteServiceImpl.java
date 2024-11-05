package com.npc.old_school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.npc.old_school.dto.course.CourseQueryDTO;
import com.npc.old_school.dto.service_site.ServiceSiteDTO;
import com.npc.old_school.dto.service_site.ServiceSiteQueryDTO;
import com.npc.old_school.dto.service_site.SiteAuditDTO;
import com.npc.old_school.dto.service_site.SiteCourseReportDTO;
import com.npc.old_school.dto.service_site.SiteReportDTO;
import com.npc.old_school.entity.ServiceSiteEntity;
import com.npc.old_school.entity.SiteImageEntity;
import com.npc.old_school.exception.BusinessException;
import com.npc.old_school.mapper.ServiceSiteMapper;
import com.npc.old_school.mapper.SiteImageMapper;
import com.npc.old_school.service.ServiceSiteService;
import com.npc.old_school.util.FileUtil;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class ServiceSiteServiceImpl implements ServiceSiteService {

    @Autowired
    private ServiceSiteMapper serviceSiteMapper;

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private SiteImageMapper siteImageMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceSiteEntity createServiceSite(ServiceSiteDTO dto, MultipartFile qrCode, List<MultipartFile> images)
            throws IOException {
        // 保存站点基本信息
        ServiceSiteEntity serviceSite = new ServiceSiteEntity();
        BeanUtils.copyProperties(dto, serviceSite);
        serviceSite.setAuditStatus(ServiceSiteEntity.AuditStatus.PENDING);

        // 处理二维码
        if (qrCode != null && !qrCode.isEmpty()) {
            String qrCodePath = fileUtil.saveFile(qrCode, "qrcodes");
            serviceSite.setQrCodePath(qrCodePath);
        }

        // 保存站点信息
        serviceSiteMapper.insert(serviceSite);

        // 处理站点图片
        if (images != null && !images.isEmpty()) {
            int order = 1;
            for (MultipartFile image : images) {
                String imagePath = fileUtil.saveFile(image, "images");
                SiteImageEntity siteImage = new SiteImageEntity();
                siteImage.setServiceSiteId(serviceSite.getId());
                siteImage.setImagePath(imagePath);
                siteImage.setImageOrder(order++);
                siteImageMapper.insert(siteImage);
            }
        }

        return serviceSite;
    }

    @Override
    public IPage<ServiceSiteEntity> queryServiceSites(ServiceSiteQueryDTO queryDTO) {
        LambdaQueryWrapper<ServiceSiteEntity> wrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        if (StringUtils.hasText(queryDTO.getSiteName())) {
            wrapper.like(ServiceSiteEntity::getSiteName, queryDTO.getSiteName());
        }
        if (StringUtils.hasText(queryDTO.getDistrict())) {
            wrapper.eq(ServiceSiteEntity::getDistrict, queryDTO.getDistrict());
        }
        if (queryDTO.getAuditStatus() != null) {
            wrapper.eq(ServiceSiteEntity::getAuditStatus, queryDTO.getAuditStatus());
        }

        // 添加ID排序
        wrapper.orderByAsc(ServiceSiteEntity::getId);

        // 分页查询
        Page<ServiceSiteEntity> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return serviceSiteMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteServiceSite(Long id) {
        ServiceSiteEntity serviceSite = serviceSiteMapper.selectById(id);
        if (serviceSite == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "站点不存在");
        }

        // 检查当前审核状态是否允许删除
        if (serviceSite.getAuditStatus() == ServiceSiteEntity.AuditStatus.APPROVED
                || serviceSite.getAuditStatus() == ServiceSiteEntity.AuditStatus.REJECTED) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "审核状态不允许删除");
        }

        // 逻辑删除站点
        serviceSiteMapper.deleteById(id);

        // 删除关联的图片记录
        LambdaQueryWrapper<SiteImageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SiteImageEntity::getServiceSiteId, id);
        siteImageMapper.delete(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceSiteEntity updateServiceSite(Long id, ServiceSiteDTO dto, MultipartFile qrCode,
            List<MultipartFile> newImages, List<Long> deleteImageIds) throws IOException {
        // 1. 检查站点是否存在
        ServiceSiteEntity serviceSite = serviceSiteMapper.selectById(id);
        if (serviceSite == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "站点不存在");
        }

        // 2. 检查状态是否允许更新
        if (ServiceSiteEntity.AuditStatus.PENDING.equals(serviceSite.getAuditStatus())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "站点正在审核中，不能修改");
        }

        // 3. 更新站点基本信息
        BeanUtils.copyProperties(dto, serviceSite);
        serviceSite.setAuditStatus(ServiceSiteEntity.AuditStatus.PENDING);

        // 4. 处理二维码
        if (qrCode != null && !qrCode.isEmpty()) {
            String qrCodePath = fileUtil.saveFile(qrCode, "qrcodes");
            serviceSite.setQrCodePath(qrCodePath);
        }

        // 5. 更新站点信息
        serviceSiteMapper.updateById(serviceSite);

        // 6. 处理要删除的图片
        if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
            siteImageMapper.deleteBatchIds(deleteImageIds);
        }

        // 7. 处理新增的图片
        if (newImages != null && !newImages.isEmpty()) {
            int maxOrder = getMaxImageOrder(serviceSite.getId());
            for (MultipartFile image : newImages) {
                String imagePath = fileUtil.saveFile(image, "images");
                SiteImageEntity siteImage = new SiteImageEntity();
                siteImage.setServiceSiteId(serviceSite.getId());
                siteImage.setImagePath(imagePath);
                siteImage.setImageOrder(++maxOrder);
                siteImageMapper.insert(siteImage);
            }
        }

        return serviceSite;
    }

    private int getMaxImageOrder(Long stationId) {
        LambdaQueryWrapper<SiteImageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SiteImageEntity::getServiceSiteId, stationId)
                .orderByDesc(SiteImageEntity::getImageOrder)
                .last("LIMIT 1");
        SiteImageEntity lastImage = siteImageMapper.selectOne(wrapper);
        return lastImage != null ? lastImage.getImageOrder() : 0;
    }

    @Override
    public ServiceSiteEntity getServiceSiteDetail(Long id) {
        ServiceSiteEntity serviceSite = serviceSiteMapper.selectById(id);
        if (serviceSite == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "站点不存在");
        }

        // 查询站点图片
        LambdaQueryWrapper<SiteImageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SiteImageEntity::getServiceSiteId, id)
                .orderByAsc(SiteImageEntity::getImageOrder);
        List<SiteImageEntity> images = siteImageMapper.selectList(wrapper);
        serviceSite.setImages(images);

        return serviceSite;
    }

    /*
     * 审核服务站点
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceSiteEntity auditServiceSite(SiteAuditDTO dto) {
        // 1. 检查站点是否存在
        ServiceSiteEntity serviceSite = serviceSiteMapper.selectById(dto.getServiceSiteId());
        if (serviceSite == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "站点不存在");
        }

        // 2. 检查状态是否允许审核
        if (!ServiceSiteEntity.AuditStatus.PENDING.equals(serviceSite.getAuditStatus())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "只能审核待审核的站点");
        }

        // TODO：3. 获取当前审核人信息

        // 4. 更新审核状态
        serviceSite.setAuditStatus(dto.getAuditStatus());
        serviceSite.setAuditRemark(dto.getAuditRemark());
        serviceSiteMapper.updateById(serviceSite);

        return serviceSite;
    }

    // private void handlePreviousVersion(ServiceSiteEntity serviceSite) {
    // if (serviceSite.getPreviousVersionId() != null) {
    // // 将原版本标记为历史版本
    // ServiceSiteEntity previousVersion =
    // serviceSiteMapper.selectById(serviceSite.getPreviousVersionId());
    // if (previousVersion != null) {
    // previousVersion.setAuditStatus(ServiceSiteEntity.AuditStatus.ARCHIVED);
    // serviceSiteMapper.updateById(previousVersion);
    // }
    // }
    // }

    /*
     * 查询审核中的服务站点
     */
    @Override
    public IPage<ServiceSiteEntity> queryAuditServiceSites(ServiceSiteEntity.AuditStatus auditStatus) {
        LambdaQueryWrapper<ServiceSiteEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceSiteEntity::getAuditStatus, auditStatus);
        wrapper.orderByDesc(ServiceSiteEntity::getCreatedAt);

        Page<ServiceSiteEntity> page = new Page<>(1, 10);
        IPage<ServiceSiteEntity> result = serviceSiteMapper.selectPage(page, wrapper);

        // 填充图片信息
        fillStationImages(result.getRecords());

        return result;
    }

    private void fillStationImages(List<ServiceSiteEntity> serviceSites) {
        if (CollectionUtils.isEmpty(serviceSites)) {
            return;
        }

        // 批量查询站点图片
        List<Long> stationIds = serviceSites.stream()
                .map(ServiceSiteEntity::getId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<SiteImageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SiteImageEntity::getServiceSiteId, stationIds)
                .orderByAsc(SiteImageEntity::getImageOrder);

        List<SiteImageEntity> allImages = siteImageMapper.selectList(wrapper);

        // 按站点ID分组
        Map<Long, List<SiteImageEntity>> imageMap = allImages.stream()
                .collect(Collectors.groupingBy(SiteImageEntity::getServiceSiteId));

        // 填充图片信息
        serviceSites.forEach(
                serviceSite -> serviceSite.setImages(imageMap.getOrDefault(serviceSite.getId(), new ArrayList<>())));
    }

    @Override
    public List<SiteReportDTO> exportSiteReport() {
        return serviceSiteMapper.getSiteReport();
    }

    @Override
    public List<SiteCourseReportDTO> exportSiteCourseReport(Long siteId) {
        return serviceSiteMapper.getSiteCourseReport(siteId, null);
    }

    @Override
    public IPage<SiteCourseReportDTO> querySiteCourses(Long siteId, CourseQueryDTO queryDTO) {
        Page<SiteCourseReportDTO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<SiteCourseReportDTO> records = serviceSiteMapper.getSiteCourseReport(siteId, queryDTO);
        page.setRecords(records);
        page.setTotal(records.size());
        return page;
    }

}
