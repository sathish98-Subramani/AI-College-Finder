package com.collegefinder.controller;

import com.collegefinder.dto.response.CollegeResponse;
import com.collegefinder.security.UserPrincipal;
import com.collegefinder.service.FavoriteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{collegeId}")
    public ResponseEntity<Void> add(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long collegeId) {
        favoriteService.addFavorite(principal.getId(), collegeId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{collegeId}")
    public ResponseEntity<Void> remove(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long collegeId) {
        favoriteService.removeFavorite(principal.getId(), collegeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<CollegeResponse> list(@AuthenticationPrincipal UserPrincipal principal) {
        return favoriteService.getFavorites(principal.getId());
    }
}
