package alivium.mapper;

import alivium.domain.entity.Product;
import alivium.domain.entity.User;
import alivium.domain.entity.Wishlist;
import alivium.model.dto.response.WishlistResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WishlistMapper {

    public Wishlist toEntity(User user, Product product) {
        return Wishlist.builder()
                .user(user)
                .product(product)
                .build();
    }

    public WishlistResponse toResponse(Wishlist wishlist) {
        if (wishlist == null) {
            return null;
        }

        String primaryImageUrl = wishlist.getProduct().getImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                .findFirst()
                .map(img -> img.getImageUrl())
                .orElse(wishlist.getProduct().getImages().stream()
                        .findFirst()
                        .map(img -> img.getImageUrl())
                        .orElse(null));

        return WishlistResponse.builder()
                .id(wishlist.getId())
                .userId(wishlist.getUser().getId())
                .username(wishlist.getUser().getFullName())
                .productId(wishlist.getProduct().getId())
                .productName(wishlist.getProduct().getName())
                .price(wishlist.getProduct().getPrice())
                .discountPrice(wishlist.getProduct().getDiscountPrice())
                .averageRating(wishlist.getProduct().getAverageRating())
                .reviewCount(wishlist.getProduct().getReviewCount())
                .productActive(wishlist.getProduct().getActive())
                .primaryImageUrl(primaryImageUrl)
                .addedAt(wishlist.getAddedAt())
                .build();
    }

    public List<WishlistResponse> toListResponse(List<Wishlist> wishlists) {
        return wishlists.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
