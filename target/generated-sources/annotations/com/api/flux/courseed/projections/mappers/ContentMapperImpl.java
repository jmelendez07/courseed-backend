package com.api.flux.courseed.projections.mappers;

import com.api.flux.courseed.persistence.documents.Content;
import com.api.flux.courseed.projections.dtos.ContentDto;
import com.api.flux.courseed.projections.dtos.SaveContentDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-14T13:00:55-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Eclipse Adoptium)"
)
@Component
public class ContentMapperImpl implements ContentMapper {

    @Override
    public ContentDto toContentDto(Content content) {
        if ( content == null ) {
            return null;
        }

        ContentDto contentDto = new ContentDto();

        contentDto.setId( content.getId() );
        contentDto.setDescription( content.getDescription() );
        contentDto.setCourseId( content.getCourseId() );

        return contentDto;
    }

    @Override
    public Content toContent(SaveContentDto saveContentDto) {
        if ( saveContentDto == null ) {
            return null;
        }

        Content content = new Content();

        content.setDescription( saveContentDto.getDescription() );
        content.setCourseId( saveContentDto.getCourseId() );

        return content;
    }
}
