package com.api.flux.courseed.services.interfaces;

import com.api.flux.courseed.projections.dtos.ContentDto;
import com.api.flux.courseed.projections.dtos.SaveContentDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InterfaceContentService {
    public Flux<ContentDto> getContentsByCourseId(String courseId);
    public Mono<ContentDto> getContentById(String id);
    public Mono<ContentDto> createContent(SaveContentDto content);
    public Mono<ContentDto> updateContent(String id, SaveContentDto content);
    public Mono<Boolean> deleteContent(String id);
}
