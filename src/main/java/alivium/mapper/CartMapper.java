package alivium.mapper;

import alivium.domain.entity.Cart;
import alivium.domain.entity.CartItem;
import alivium.domain.entity.ProductImage;
import alivium.model.dto.response.CartItemResponse;
import alivium.model.dto.response.CartResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    public CartItemResponse toItemResponse(CartItem item) {
        if (item == null) {
            return null;
        }

        String imageUrl = item.getProduct().getImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElse(item.getProduct().getImages().stream()
                        .findFirst()
                        .map(ProductImage::getImageUrl)
                        .orElse(null));

        BigDecimal totalPrice = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productImage(imageUrl)
                .variantId(item.getVariant() != null ? item.getVariant().getId() : null)
                .variantColor(item.getVariant() != null ? item.getVariant().getColor() : null)
                .variantSize(item.getVariant() != null ? item.getVariant().getSize() : null)
                .quantity(item.getQuantity())
                .productPrice(item.getPrice())
                .totalPrice(totalPrice)
                .productActive(item.getProduct().getActive())
                .variantAvailable(item.getVariant() != null ? item.getVariant().getAvailable() : true)
                .addedAt(item.getAddedAt())
                .build();
    }

    public CartResponse toResponse(Cart cart) {
        if (cart == null) return null;

        List<CartItemResponse> itemResponses = cart.getCartItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        BigDecimal totalPrice = cart.getCartItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(itemResponses)
                .totalItems(cart.getCartItems().size())
                .totalPrice(totalPrice)
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }
}
