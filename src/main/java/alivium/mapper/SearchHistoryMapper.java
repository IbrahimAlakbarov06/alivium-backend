package alivium.mapper;

import alivium.domain.entity.SearchHistory;
import alivium.model.dto.response.SearchHistoryResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SearchHistoryMapper {

    public SearchHistoryResponse toResponse(SearchHistory history) {
        if (history == null) return null;

        return SearchHistoryResponse.builder()
                .id(history.getId())
                .query(history.getSearchQuery())
                .resultCount(history.getResultCount())
                .searchedAt(history.getSearchedAt())
                .build();
    }

    public List<SearchHistoryResponse> toListResponse(List<SearchHistory> histories) {
        if (histories == null) return List.of();

        return histories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}