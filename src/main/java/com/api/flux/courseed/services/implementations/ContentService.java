package com.api.flux.courseed.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.flux.courseed.persistence.repositories.ContentRepository;
import com.api.flux.courseed.persistence.repositories.CourseRepository;
import com.api.flux.courseed.projections.dtos.ContentDto;
import com.api.flux.courseed.projections.dtos.SaveContentDto;
import com.api.flux.courseed.projections.mappers.ContentMapper;
import com.api.flux.courseed.services.interfaces.InterfaceContentService;
import com.api.flux.courseed.web.exceptions.CustomWebExchangeBindException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ContentService implements InterfaceContentService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ContentMapper contentMapper;

    @Override
    public Mono<ContentDto> getContentById(String id) {
        return contentRepository.findById(id)
            .map(contentMapper::toContentDto)
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    id, 
                    "contentId", 
                    "No hemos podido encontrar el contenido indicado. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException())
            );
    }

    @Override
    public Flux<ContentDto> getContentsByCourseId(String courseId) {
        return contentRepository.findByCourseId(courseId)
            .map(contentMapper::toContentDto)
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    courseId, 
                    "courseId", 
                    "No se encontraron contenidos del curso indicado. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException())
            ); 
    }

    @Override
    public Mono<ContentDto> createContent(SaveContentDto saveContentDto) {
        return courseRepository.findById(saveContentDto.getCourseId())
            .flatMap(course -> contentRepository.save(contentMapper.toContent(saveContentDto))
                .map(contentMapper::toContentDto)
            )
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    saveContentDto.getCourseId(), 
                    "courseId", 
                    "No hemos podido encontrar el curso indicado. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException())
            );
    }

    @Override
    public Mono<ContentDto> updateContent(String id, SaveContentDto saveContentDto) {
        return contentRepository.findById(id)
            .flatMap(content -> courseRepository.findById(saveContentDto.getCourseId())
                .flatMap(course -> {
                    content.setCourseId(saveContentDto.getCourseId());
                    content.setDescription(saveContentDto.getDescription());
                    return contentRepository.save(content)
                        .map(contentMapper::toContentDto);
                })
                .switchIfEmpty(Mono.error(
                    new CustomWebExchangeBindException(
                        saveContentDto.getCourseId(), 
                        "courseId", 
                        "No hemos podido encontrar el curso indicado. Te sugerimos que verifiques la información y lo intentes de nuevo."
                    ).getWebExchangeBindException())
                )
            )
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    id, 
                    "contentId", 
                    "No hemos podido encontrar el contenido indicado. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException())
            );
    }

    @Override
    public Mono<Boolean> deleteContent(String id) {
        return contentRepository.findById(id)
            .flatMap(content -> 
                contentRepository.deleteById(id)
                    .then(Mono.just(true))
            )
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    id, 
                    "contentId", 
                    "No hemos podido encontrar el contenido indicado. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException())
            );
    }

}
