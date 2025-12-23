package alivium.controller;

import alivium.domain.entity.User;
import alivium.model.dto.request.ProductSearchRequest;
import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.ProductResponse;
import alivium.model.dto.response.SearchHistoryResponse;
import alivium.service.SearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchHistoryController {

    private final SearchService searchService;

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> searchProducts(
            @AuthenticationPrincipal User user,
            @RequestParam String query){
        Long userId = user != null ? user.getId() :null;
        return ResponseEntity.ok(searchService.searchProducts(userId, query));
    }

    @PostMapping("/products/filter")
    public ResponseEntity<List<ProductResponse>> searchWithFilters(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ProductSearchRequest request){
        Long userId = user != null ? user.getId() : null;
        return ResponseEntity.ok(searchService.searchWithFilters(userId, request));
    }
    @GetMapping("/history")
    public ResponseEntity<List<SearchHistoryResponse>> getMySearchHistory(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(searchService.getUserSearchHistory(user.getId()));
    }

    @GetMapping("/history/top-10")
    public ResponseEntity<List<SearchHistoryResponse>> getTop10MySearchHistory(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(searchService.getTop10UserSearchHistory(user.getId()));
    }


    @DeleteMapping("/history/clear")
    public ResponseEntity<MessageResponse> clearSearchHistory(
            @AuthenticationPrincipal User user) {
        searchService.clearUserSearchHistory(user.getId());
        return ResponseEntity.ok(new MessageResponse("Search history cleared successfully"));
    }

    @DeleteMapping("/history/{historyId}")
    public ResponseEntity<MessageResponse> deleteSearchHistory(
            @AuthenticationPrincipal User user,
            @PathVariable Long historyId) {
        searchService.deleteSearchHistory(historyId, user.getId());
        return ResponseEntity.ok(new MessageResponse("Search history deleted successfully"));
    }
}