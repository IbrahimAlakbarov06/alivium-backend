package alivium.controller;

import alivium.domain.entity.User;
import alivium.model.dto.request.AddToCartRequest;
import alivium.model.dto.request.UpdateCartItemRequest;
import alivium.model.dto.response.CartResponse;
import alivium.model.dto.response.MessageResponse;
import alivium.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Validated
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getMyCart(@AuthenticationPrincipal User user) {
        CartResponse cart = cartService.getOrCreateUserCart(user.getId());
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getCartItemCount(@AuthenticationPrincipal User user) {
        Long count = cartService.getCartItemCount(user.getId());
        return ResponseEntity.ok(count);
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItemToCart(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddToCartRequest request) {
        CartResponse cart = cartService.addItemToCart(user.getId(), request);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartResponse cart = cartService.updateCartItem(user.getId(), itemId, request);
        return ResponseEntity.ok(cart);
    }


    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItemFromCart(
            @AuthenticationPrincipal User user,
            @PathVariable Long itemId) {
        CartResponse cart = cartService.removeItemFromCart(user.getId(), itemId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<MessageResponse> clearCart(@AuthenticationPrincipal User user) {
        MessageResponse response = cartService.clearCart(user.getId());
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/admin/user/{userId}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<MessageResponse> deleteUserCart(@PathVariable Long userId) {
        MessageResponse response = cartService.deleteCartByUserId(userId);
        return ResponseEntity.ok(response);
    }
}