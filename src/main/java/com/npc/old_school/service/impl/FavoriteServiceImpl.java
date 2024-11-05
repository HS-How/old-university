package com.npc.old_school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.npc.old_school.dto.favorite.FavoriteDTO;
import com.npc.old_school.dto.favorite.FavoriteQueryDTO;
import com.npc.old_school.entity.FavoriteEntity;
import com.npc.old_school.entity.FavoriteEntity.FavoriteType;
import com.npc.old_school.exception.BusinessException;
import com.npc.old_school.mapper.CourseMapper;
import com.npc.old_school.mapper.FavoriteMapper;
import com.npc.old_school.mapper.ServiceSiteMapper;
import com.npc.old_school.service.FavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private ServiceSiteMapper serviceSiteMapper;

    @Override
    @Transactional
    public FavoriteEntity addFavorite(FavoriteDTO favoriteDTO) {
        // 1. 检查是否已经收藏
        LambdaQueryWrapper<FavoriteEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FavoriteEntity::getUserName, favoriteDTO.getUserName())
                .eq(FavoriteEntity::getTargetId, favoriteDTO.getTargetId())
                .eq(FavoriteEntity::getType, favoriteDTO.getType());
                
        if (favoriteMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "已经收藏过了");
        }
        
        // 2. 检查目标是否存在
        FavoriteType type = favoriteDTO.getType();
        if (type == FavoriteType.COURSE) {
            if (courseMapper.selectById(favoriteDTO.getTargetId()) == null) {
                throw new BusinessException(HttpStatus.NOT_FOUND, "课程不存在");
            }
        } else if (type == FavoriteType.SERVICE_SITE) {
            if (serviceSiteMapper.selectById(favoriteDTO.getTargetId()) == null) {
                throw new BusinessException(HttpStatus.NOT_FOUND, "服务站点不存在");
            }
        }
        
        // 3. 创建收藏
        FavoriteEntity favorite = new FavoriteEntity();
        BeanUtils.copyProperties(favoriteDTO, favorite);
        favorite.setType(type);
        favoriteMapper.insert(favorite);
        
        return favorite;
    }

    @Override
    public void deleteFavorite(Long id) {
        FavoriteEntity favorite = favoriteMapper.selectById(id);
        if (favorite == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "收藏记录不存在");
        }
        favoriteMapper.deleteById(id);
    }

    @Override
    public IPage<FavoriteEntity> queryFavorites(FavoriteQueryDTO queryDTO) {
        LambdaQueryWrapper<FavoriteEntity> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(queryDTO.getUserName())) {
            wrapper.eq(FavoriteEntity::getUserName, queryDTO.getUserName());
        }
        if (queryDTO.getType() != null) {
            wrapper.eq(FavoriteEntity::getType, queryDTO.getType());
        }
        
        Page<FavoriteEntity> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<FavoriteEntity> resultPage = favoriteMapper.selectPage(page, wrapper);
        
        // 加载关联的课程或服务站点信息
        resultPage.getRecords().forEach(favorite -> {
            if (favorite.getType() == FavoriteType.COURSE) {
                favorite.setCourse(courseMapper.selectById(favorite.getTargetId()));
            } else if (favorite.getType() == FavoriteType.SERVICE_SITE) {
                favorite.setServiceSite(serviceSiteMapper.selectById(favorite.getTargetId()));
            }
        });
        
        return resultPage;
    }
} 