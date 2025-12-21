package alivium.service.impl;

import alivium.domain.entity.Product;
import alivium.domain.entity.SearchHistory;
import alivium.domain.entity.User;
import alivium.domain.repository.ProductRepository;
import alivium.domain.repository.SearchHistoryRepository;
import alivium.domain.repository.UserRepository;
import alivium.exception.NotFoundException;
import alivium.mapper.ProductMapper;
import alivium.mapper.SearchHistoryMapper;
import alivium.model.dto.request.ProductSearchRequest;
import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.ProductResponse;
import alivium.model.dto.response.SearchHistoryResponse;
import alivium.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchHistoryServiceImpl implements SearchService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final UserRepository userRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final SearchHistoryMapper searchHistoryMapper;

    @Override
    @Transactional
    public List<ProductResponse> searchProducts(Long userId, String query) {
        if (query == null || query.isEmpty()) {
            return List.of();
        }

        List<Product> products = productRepository.searchProducts(query);

        if (userId != null) {
            saveSearchHistory(userId, query, products.size());
        }

        return productMapper.toListResponse(products);
    }

    @Override
    @Transactional
    public List<ProductResponse> searchWithFilters(Long userId, ProductSearchRequest request) {
        List<Product> products= productRepository.searchWithFilters(
                request.getQuery(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getCategoryIds(),
                request.getCollectionIds(),
                request.getMinRating(),
                request.getColors(),
                request.getSizes(),
                request.getInStock()
        );

        if (request.getSortBy() != null) {
            products =sortProducts(products, request.getSortBy(), request.getSortOrder());
        }

        if (userId != null) {
            saveSearchHistory(userId, request.getQuery(), products.size());
        }

        return productMapper.toListResponse(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchHistoryResponse> getUserSearchHistory(Long userId) {
        List<SearchHistory> history= searchHistoryRepository
                .findByUserIdOrderBySearchedAtDesc(userId);

        return searchHistoryMapper.toListResponse(history);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchHistoryResponse> getTop10UserSearchHistory(Long userId) {
        List<SearchHistory> history= searchHistoryRepository
                .findTop10ByUserIdOrderBySearchedAtDesc(userId);

        return searchHistoryMapper.toListResponse(history);
    }

    @Override
    @Transactional
    public void clearUserSearchHistory(Long userId) {
        searchHistoryRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteSearchHistory(Long historyId, Long userId) {
        SearchHistory history = searchHistoryRepository.findById(historyId)
                .orElseThrow(() -> new NotFoundException("Search history not found"));

        if (!history.getUser().getId().equals(userId)) {
            throw new NotFoundException("Search history not found");
        }

        searchHistoryRepository.delete(history);
    }


    private void saveSearchHistory(Long userId, String query, Integer resultCount) {
            User user =userRepository.findById(userId).orElse(null);
            if (user==null) return;

            SearchHistory searchHistory =SearchHistory.builder()
                    .searchQuery(query)
                    .user(user)
                    .resultCount(resultCount)
                    .build();
            searchHistoryRepository.save(searchHistory);
    }

    private List<Product> sortProducts(List<Product> products, String sortBy, String sortOrder) {
        Comparator<Product> comparator = switch (sortBy.toLowerCase()) {
            case "price" -> Comparator.comparing(Product::getPrice);
            case "rating" -> Comparator.comparing(Product::getAverageRating);
            case "newest" -> Comparator.comparing(Product::getCreatedAt);
            case "name" -> Comparator.comparing(Product::getName);
            case "popularity" -> Comparator.comparing(Product::getReviewCount);
            default -> Comparator.comparing(Product::getId);
        };

        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }

        return products.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}
