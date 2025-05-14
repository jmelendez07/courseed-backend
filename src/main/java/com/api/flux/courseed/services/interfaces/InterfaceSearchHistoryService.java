package com.api.flux.courseed.services.interfaces;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;

import com.api.flux.courseed.projections.dtos.SaveSearchHistoryDto;
import com.api.flux.courseed.projections.dtos.SearchHistoryDto;

import reactor.core.publisher.Mono;

public interface InterfaceSearchHistoryService {
    Mono<Page<SearchHistoryDto>> findByAuthUser(Principal principal, String search, int page, int size);
    Mono<SearchHistoryDto> createSearchHistory(Principal principal, SaveSearchHistoryDto saveSearchHistoryDto);
    Mono<Boolean> deleteSearchHistory(Principal principal, String id);
    Mono<Boolean> deleteSearchHistories(Principal principal, List<String> ids);
}
