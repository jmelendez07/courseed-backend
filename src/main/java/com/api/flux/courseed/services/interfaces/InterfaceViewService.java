package com.api.flux.courseed.services.interfaces;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;

import com.api.flux.courseed.projections.dtos.CourseViewsStatsDto;
import com.api.flux.courseed.projections.dtos.SaveViewDto;
import com.api.flux.courseed.projections.dtos.TotalViewsDto;
import com.api.flux.courseed.projections.dtos.ViewDto;

import reactor.core.publisher.Mono;

public interface InterfaceViewService {
    Mono<TotalViewsDto> getTotalViews();
    Mono<List<CourseViewsStatsDto>> findCoursesWithDecreasingViews();
    Mono<Page<ViewDto>> findViewsByCourseId(String courseId, int page, int size);
    Mono<Page<ViewDto>> findViewsByAuthUser(Principal principal, int page, int size, String search);
    Mono<Object> createView(Principal principal, SaveViewDto saveViewDto);
    Mono<Integer> getTotalViewsBySuscriptor(Principal principal);
}
