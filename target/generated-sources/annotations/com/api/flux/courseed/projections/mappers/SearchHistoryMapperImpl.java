package com.api.flux.courseed.projections.mappers;

import com.api.flux.courseed.persistence.documents.SearchHistory;
import com.api.flux.courseed.projections.dtos.SaveSearchHistoryDto;
import com.api.flux.courseed.projections.dtos.SearchHistoryDto;
import com.api.flux.courseed.projections.dtos.UserDto;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-14T13:00:55-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Eclipse Adoptium)"
)
@Component
public class SearchHistoryMapperImpl implements SearchHistoryMapper {

    @Override
    public SearchHistoryDto toSearchHistoryDto(SearchHistory searchHistory) {
        if ( searchHistory == null ) {
            return null;
        }

        String id = null;
        String search = null;
        LocalDateTime createdAt = null;

        id = searchHistory.getId();
        search = searchHistory.getSearch();
        createdAt = searchHistory.getCreatedAt();

        UserDto user = null;

        SearchHistoryDto searchHistoryDto = new SearchHistoryDto( id, user, search, createdAt );

        return searchHistoryDto;
    }

    @Override
    public SearchHistory toSearchHistory(SaveSearchHistoryDto saveSearchHistoryDto) {
        if ( saveSearchHistoryDto == null ) {
            return null;
        }

        SearchHistory searchHistory = new SearchHistory();

        searchHistory.setSearch( saveSearchHistoryDto.getSearch() );

        return searchHistory;
    }
}
