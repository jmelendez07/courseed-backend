package com.api.flux.courseed.projections.mappers;

import com.api.flux.courseed.persistence.documents.Profile;
import com.api.flux.courseed.projections.dtos.ProfileDto;
import com.api.flux.courseed.projections.dtos.SaveProfileDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-14T13:00:55-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Eclipse Adoptium)"
)
@Component
public class ProfileMapperImpl implements ProfileMapper {

    @Override
    public ProfileDto toProfileDto(Profile profile) {
        if ( profile == null ) {
            return null;
        }

        ProfileDto profileDto = new ProfileDto();

        profileDto.setKnowledgeLevel( profile.getKnowledgeLevel() );
        profileDto.setAvailableHoursTime( profile.getAvailableHoursTime() );
        profileDto.setPlatformPrefered( profile.getPlatformPrefered() );
        profileDto.setBudget( profile.getBudget() );
        profileDto.setCreatedAt( profile.getCreatedAt() );
        profileDto.setUpdatedAt( profile.getUpdatedAt() );
        profileDto.setInterest( profile.getInterest() );
        profileDto.setId( profile.getId() );

        return profileDto;
    }

    @Override
    public Profile toProfile(SaveProfileDto saveProfileDto) {
        if ( saveProfileDto == null ) {
            return null;
        }

        Profile profile = new Profile();

        profile.setInterest( saveProfileDto.getInterest() );
        profile.setKnowledgeLevel( saveProfileDto.getKnowledgeLevel() );
        profile.setAvailableHoursTime( saveProfileDto.getAvailableHoursTime() );
        profile.setPlatformPrefered( saveProfileDto.getPlatformPrefered() );
        profile.setBudget( saveProfileDto.getBudget() );

        return profile;
    }
}
