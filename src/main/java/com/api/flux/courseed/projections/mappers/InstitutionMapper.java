package com.api.flux.courseed.projections.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.api.flux.courseed.persistence.documents.Institution;
import com.api.flux.courseed.projections.dtos.InstitutionDto;
import com.api.flux.courseed.projections.dtos.SaveInstitutionDto;

@Mapper(componentModel = "spring")
public interface InstitutionMapper {

    @Mapping(target = "user", ignore = true)
    InstitutionDto toInstitutionDto(Institution institution);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "userId", ignore = true)
    Institution toInstitution(SaveInstitutionDto saveInstitutionDto);
}
