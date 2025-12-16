package alivium.service;

import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.WishlistResponse;

import java.util.List;

public interface WishlistService {

    WishlistResponse addProductToWishlist(Long userId, Long productId);

    List<WishlistResponse> getUserWishlists(Long userId);

    Long getWishlistCount(Long userId);

    Boolean isInWishlist(Long userId, Long productId);

    MessageResponse removeProductFromWishlist(Long userId, Long productId);

    MessageResponse clearWishlist(Long userId);
}
