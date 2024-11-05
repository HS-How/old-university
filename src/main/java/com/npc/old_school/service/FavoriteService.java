package com.npc.old_school.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.npc.old_school.dto.favorite.FavoriteDTO;
import com.npc.old_school.dto.favorite.FavoriteQueryDTO;
import com.npc.old_school.entity.FavoriteEntity;

public interface FavoriteService {
    FavoriteEntity addFavorite(FavoriteDTO favoriteDTO);
    void deleteFavorite(Long id);
    IPage<FavoriteEntity> queryFavorites(FavoriteQueryDTO queryDTO);
} 