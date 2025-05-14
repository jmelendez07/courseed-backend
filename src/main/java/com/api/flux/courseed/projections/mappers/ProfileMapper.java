package com.api.flux.courseed.projections.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.api.flux.courseed.persistence.documents.Profile;
import com.api.flux.courseed.projections.dtos.ProfileDto;
import com.api.flux.courseed.projections.dtos.SaveProfileDto;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    ProfileDto toProfileDto(Profile profile);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "platformPreference", ignore = true)
    Profile toProfile(SaveProfileDto saveProfileDto);
}
