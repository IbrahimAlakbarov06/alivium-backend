package alivium.service.impl;

import alivium.domain.entity.*;
import alivium.domain.repository.*;
import alivium.exception.BusinessException;
import alivium.exception.NotFoundException;
import alivium.mapper.CartMapper;
import alivium.model.dto.request.AddToCartRequest;
import alivium.model.dto.request.UpdateCartItemRequest;
import alivium.model.dto.response.CartResponse;
import alivium.model.dto.response.MessageResponse;
import alivium.service.CartService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;
    private final EntityManager entityManager;

    @Override
    @Transactional
    @Cacheable(value = "carts", key = "#userId")
    public CartResponse getOrCreateUserCart(Long userId) {
        User user = findUserById(userId);

        Cart cart =cartRepository.findByUserIdWithItems(userId)
                .orElseGet(()->{
                        Cart newCart = Cart.builder()
                                .user(user)
                                .build();
                        return cartRepository.save(newCart);
                });

        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    @CacheEvict(value = "carts", key = "#userId")
    public CartResponse addItemToCart(Long userId, AddToCartRequest request) {
        Cart cart =getOrCreateCart(userId);
        Product product = findProductById(request.getProductId());

        if (!product.getActive()){
            throw new BusinessException("Product is not active");
        }

        ProductVariant variant=null;
        if (request.getVariantId() != null) {
            variant = findVariantById(request.getVariantId());

            if (!variant.getAvailable()) {
                throw new BusinessException("Product variant is not available");
            }

            if (variant.getStockQuantity() < request.getQuantity()) {
                throw new BusinessException("Insufficient stock. Available: " + variant.getStockQuantity());
            }
        }

        BigDecimal price =product.getDiscountPrice() !=null
                ? product.getDiscountPrice() : product.getPrice();

        if (variant !=null && variant.getAdditionalPrice()!=null) {
            price = price.add(variant.getAdditionalPrice());
        }

        CartItem existingItem =cartItemRepository.findByCartIdAndProductIdAndVariantId(
                cart.getId(), request.getProductId(), request.getVariantId())
                .orElse(null);

        if (existingItem!=null) {
            int newQuantity = existingItem.getQuantity() + request.getQuantity();

            if (variant != null && variant.getStockQuantity() < newQuantity) {
                throw new BusinessException("Insufficient stock. Available: " + variant.getStockQuantity());
            }
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        }else {
            CartItem cartItem=cartMapper.toEntity(cart, product, variant, request, price);
            cartItemRepository.save(cartItem);
        }
        entityManager.flush();
        entityManager.clear();

        Cart updatedCart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found"));

        return cartMapper.toResponse(updatedCart);
    }

    @Override
    @Transactional
    @CacheEvict(value = "carts", key = "#userId")
    public CartResponse updateCartItem(Long userId,Long itemId, UpdateCartItemRequest request) {
        Cart cart =getOrCreateCart(userId);

        CartItem item = cartItemRepository.findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() -> new NotFoundException("Cart item not found"));

        if (item.getVariant() != null) {
            if (item.getVariant().getStockQuantity()< request.getQuantity()) {
                throw new BusinessException("Insufficient stock. Available: " + item.getVariant().getStockQuantity());
            }
        }

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        Cart updated = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found"));

        return cartMapper.toResponse(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = "carts", key = "#userId")
    public CartResponse removeItemFromCart(Long userId, Long itemId) {
        Cart cart =getOrCreateCart(userId);

        CartItem item=cartItemRepository.findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() -> new NotFoundException("Cart item not found"));

        cartItemRepository.delete(item);

        Cart updated = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found"));

        return cartMapper.toResponse(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = "carts", key = "#userId")
    public MessageResponse clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCartId(cart.getId());

        return new MessageResponse("Cart cleared successfully");
    }


    @Override
    @Transactional(readOnly = true)
    public Long getCartItemCount(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElse(null);
        if (cart == null) {
            return 0L;
        }

        return cartItemRepository.countByCartId(cart.getId());
    }

    @Override
    @Transactional
    @CacheEvict(value = "carts",key = "#userId")
    public MessageResponse deleteCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found with userId: " + userId));

        cartItemRepository.deleteByCartId(cart.getId());
        cartRepository.delete(cart);

        return new MessageResponse("Cart deleted successfully with userId: " + userId);
    }

    private Cart getOrCreateCart(Long userId) {
        User user = findUserById(userId);

        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    private ProductVariant findVariantById(Long variantId) {
        return variantRepository.findById(variantId)
                .orElseThrow(() -> new NotFoundException("Variant not found with id: " + variantId));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));
    }
}
