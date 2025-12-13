package alivium.service;

import alivium.domain.entity.Product;
import alivium.domain.entity.User;
import alivium.domain.entity.Wishlist;
import alivium.domain.repository.ProductRepository;
import alivium.domain.repository.UserRepository;
import alivium.domain.repository.WishlistRepository;
import alivium.exception.AlreadyExistsException;
import alivium.exception.NotFoundException;
import alivium.mapper.WishlistMapper;
import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.WishlistResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final WishlistMapper wishlistMapper;

    @Transactional
    @CacheEvict(value = "wishlist", key = "#userId")
    public WishlistResponse addProductToWishlist(Long userId, Long productId) {
        User user = findUserById(userId);
        Product product = findProductById(productId);

        if (wishlistRepository.existsByProductIdAndUserId(productId, userId)) {
            throw new AlreadyExistsException("Product is already in wishlist");
        }

        Wishlist wishlist = wishlistMapper.toEntity(user, product);
        return wishlistMapper.toResponse(wishlistRepository.save(wishlist));
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "wishlist", key = "#userId")
    public List<WishlistResponse> getUserWishlists(Long userId) {
       List<Wishlist> wishlists = wishlistRepository.findByUserIdOrderByAddedAtDesc(userId);
       return wishlistMapper.toListResponse(wishlists);
    }


    @Transactional(readOnly = true)
    public Long getWishlistCount(Long userId) {
        return wishlistRepository.countByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Boolean isInWishlist(Long userId, Long productId) {
        return wishlistRepository.existsByProductIdAndUserId(productId, userId);
    }

    @Transactional
    @CacheEvict(value = "wishlist", key = "#userId")
    public MessageResponse removeProductFromWishlist(Long userId, Long productId) {
        if (!wishlistRepository.existsByProductIdAndUserId(productId, userId)) {
            throw new NotFoundException("Product is not in wishlist");
        }

        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
        return new MessageResponse("Product removed from wishlist successfully");
    }

    @Transactional
    @CacheEvict(value = "wishlist",key = "#userId")
    public MessageResponse clearWishlist(Long userId) {
        List<Wishlist> wishlists = wishlistRepository.findByUserIdOrderByAddedAtDesc(userId);

        if (wishlists.isEmpty()) {
            throw new NotFoundException("Wishlist is already empty");
        }

        wishlistRepository.deleteAll(wishlists);
        return new MessageResponse("Wishlist cleared successfully");
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }
}
