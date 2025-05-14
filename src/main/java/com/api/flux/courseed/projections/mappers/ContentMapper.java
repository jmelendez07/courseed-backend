package com.api.flux.courseed.projections.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.api.flux.courseed.persistence.documents.Content;
import com.api.flux.courseed.projections.dtos.ContentDto;
import com.api.flux.courseed.projections.dtos.SaveContentDto;

@Mapper(componentModel = "spring")
public interface ContentMapper {
    ContentDto toContentDto(Content content);

    @Mapping(target = "id", ignore = true)
    Content toContent(SaveContentDto saveContentDto);
}
