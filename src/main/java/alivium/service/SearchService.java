package alivium.service;

import alivium.model.dto.request.ProductSearchRequest;
import alivium.model.dto.response.ProductResponse;
import alivium.model.dto.response.SearchHistoryResponse;

import java.util.List;

public interface SearchService {

    List<ProductResponse> searchProducts(Long userId, String query);

    List<ProductResponse> searchWithFilters(Long userId, ProductSearchRequest request);

    List<SearchHistoryResponse> getUserSearchHistory(Long userId);

    void clearUserSearchHistory(Long userId);

    List<SearchHistoryResponse> getTop10UserSearchHistory(Long userId);

    void deleteSearchHistory(Long historyId, Long userId);
}