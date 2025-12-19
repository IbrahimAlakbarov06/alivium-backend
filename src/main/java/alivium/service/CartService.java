package alivium.service;

import alivium.model.dto.request.AddToCartRequest;
import alivium.model.dto.request.UpdateCartItemRequest;
import alivium.model.dto.response.CartResponse;
import alivium.model.dto.response.MessageResponse;

public interface CartService {

    CartResponse getOrCreateUserCart(Long userId);

    CartResponse addItemToCart(Long userId, AddToCartRequest request);

    CartResponse updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request);

    CartResponse removeItemFromCart(Long userId, Long itemId);

    MessageResponse clearCart(Long userId);

    Long getCartItemCount(Long userId);

    MessageResponse deleteCartByUserId(Long userId);
}