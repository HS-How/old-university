package com.npc.old_school.controller;

import com.npc.old_school.dto.favorite.FavoriteDTO;
import com.npc.old_school.dto.favorite.FavoriteQueryDTO;
import com.npc.old_school.exception.ResultResponse;
import com.npc.old_school.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    @PostMapping
    public ResultResponse addFavorite(@Valid @RequestBody FavoriteDTO favoriteDTO) {
        return ResultResponse.success(favoriteService.addFavorite(favoriteDTO));
    }

    @DeleteMapping("/{id}")
    public ResultResponse deleteFavorite(@PathVariable Long id) {
        favoriteService.deleteFavorite(id);
        return ResultResponse.success();
    }

    @GetMapping
    public ResultResponse queryFavorites(@Valid FavoriteQueryDTO queryDTO) {
        return ResultResponse.success(favoriteService.queryFavorites(queryDTO));
    }
} 