package com.api.flux.courseed.projections.mappers;

import com.api.flux.courseed.persistence.documents.Institution;
import com.api.flux.courseed.projections.dtos.InstitutionDto;
import com.api.flux.courseed.projections.dtos.SaveInstitutionDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-14T13:00:55-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Eclipse Adoptium)"
)
@Component
public class InstitutionMapperImpl implements InstitutionMapper {

    @Override
    public InstitutionDto toInstitutionDto(Institution institution) {
        if ( institution == null ) {
            return null;
        }

        InstitutionDto institutionDto = new InstitutionDto();

        institutionDto.setId( institution.getId() );
        institutionDto.setName( institution.getName() );
        institutionDto.setImage( institution.getImage() );

        return institutionDto;
    }

    @Override
    public Institution toInstitution(SaveInstitutionDto saveInstitutionDto) {
        if ( saveInstitutionDto == null ) {
            return null;
        }

        Institution institution = new Institution();

        institution.setName( saveInstitutionDto.getName() );

        return institution;
    }
}
