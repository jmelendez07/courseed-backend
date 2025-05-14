package com.api.flux.courseed.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.api.flux.courseed.projections.dtos.ContentDto;
import com.api.flux.courseed.projections.dtos.SaveContentDto;
import com.api.flux.courseed.services.implementations.ContentService;
import com.api.flux.courseed.services.implementations.ValidationService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

public class ContentController {

    @Autowired
    private ContentService contentService;

    @Autowired
    private ValidationService validationService;

    public Mono<ServerResponse> getContentById(ServerRequest serverRequest) {
        return contentService.getContentById(serverRequest.pathVariable("id"))
            .flatMap(content -> ServerResponse.ok().bodyValue(content))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getContentByCourseId(ServerRequest serverRequest) {
        return contentService.getContentsByCourseId(serverRequest.pathVariable("courseId"))
            .collectList().flatMap(list -> {
                if (!list.isEmpty()) {
                    return ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Flux.fromIterable(list), ContentDto.class);
                } else {
                    return ServerResponse.notFound().build();
                }
            });
    }

    public Mono<ServerResponse> createContent(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(SaveContentDto.class)
            .doOnNext(validationService::validate)
            .flatMap(saveContentDto -> contentService.createContent(saveContentDto)
                .flatMap(contentDto -> ServerResponse.ok().bodyValue(contentDto))  
                .switchIfEmpty(ServerResponse.notFound().build())
            );
    }

    public Mono<ServerResponse> updateContent(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(SaveContentDto.class)
            .doOnNext(validationService::validate)
            .flatMap(saveContentDto -> contentService.updateContent(serverRequest.pathVariable("id"), saveContentDto)
                .flatMap(contentDto -> ServerResponse.ok().bodyValue(contentDto))
                .switchIfEmpty(ServerResponse.notFound().build())
            );
    }

    public Mono<ServerResponse> deleteContent(ServerRequest serverRequest) {
        return contentService.deleteContent(serverRequest.pathVariable("id"))
            .flatMap(c -> ServerResponse.ok().bodyValue(c))
            .switchIfEmpty(ServerResponse.notFound().build());
    }
}
