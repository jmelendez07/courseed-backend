package com.api.flux.courseed.projections.mappers;

import com.api.flux.courseed.persistence.documents.View;
import com.api.flux.courseed.projections.dtos.SaveViewDto;
import com.api.flux.courseed.projections.dtos.ViewDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-05T12:22:52-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.z20250331-1358, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class ViewMapperImpl implements ViewMapper {

    @Override
    public ViewDto toViewDto(View view) {
        if ( view == null ) {
            return null;
        }

        ViewDto viewDto = new ViewDto();

        viewDto.setId( view.getId() );
        viewDto.setCreatedAt( view.getCreatedAt() );

        return viewDto;
    }

    @Override
    public View toView(SaveViewDto saveViewDto) {
        if ( saveViewDto == null ) {
            return null;
        }

        View view = new View();

        view.setCourseId( saveViewDto.getCourseId() );

        return view;
    }
}
