package alivium.controller;

import alivium.domain.entity.User;
import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.WishlistResponse;
import alivium.service.WishlistService;
import alivium.service.impl.WishlistServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Validated
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/product/{productId}")
    public ResponseEntity<WishlistResponse> addToMyWishlist(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId) {
        WishlistResponse response = wishlistService.addProductToWishlist(user.getId(), productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<WishlistResponse>> getMyWishlist(
            @AuthenticationPrincipal User user) {
        List<WishlistResponse> wishlist = wishlistService.getUserWishlists(user.getId());
        return ResponseEntity.ok(wishlist);
    }
    @GetMapping("/count")
    public ResponseEntity<Long> getWishlistCount(
            @AuthenticationPrincipal User user) {
        Long count = wishlistService.getWishlistCount(user.getId());
        return ResponseEntity.ok(count);
    }

    @GetMapping("/product/{productId}/exists")
    public ResponseEntity<Boolean> isInMyWishlist(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId) {
        boolean exists = wishlistService.isInWishlist(user.getId(), productId);
        return ResponseEntity.ok(exists);
    }


    @DeleteMapping("/product/{productId}")
    public ResponseEntity<MessageResponse> removeFromMyWishlist(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId) {
        MessageResponse response = wishlistService.removeProductFromWishlist(user.getId(), productId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<MessageResponse> clearMyWishlist(
            @AuthenticationPrincipal User user) {
        MessageResponse response = wishlistService.clearWishlist(user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<List<WishlistResponse>> getUserWishlist(@PathVariable Long userId) {
        List<WishlistResponse> wishlist = wishlistService.getUserWishlists(userId);
        return ResponseEntity.ok(wishlist);
    }


    @DeleteMapping("/user/{userId}/clear")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<MessageResponse> clearUserWishlist(@PathVariable Long userId) {
        MessageResponse response = wishlistService.clearWishlist(userId);
        return ResponseEntity.ok(response);
    }
}