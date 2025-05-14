package com.api.flux.courseed.projections.mappers;

import com.api.flux.courseed.persistence.documents.Profile;
import com.api.flux.courseed.projections.dtos.ProfileDto;
import com.api.flux.courseed.projections.dtos.SaveProfileDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-05T13:22:57-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.z20250331-1358, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class ProfileMapperImpl implements ProfileMapper {

    @Override
    public ProfileDto toProfileDto(Profile profile) {
        if ( profile == null ) {
            return null;
        }

        ProfileDto profileDto = new ProfileDto();

        profileDto.setAvailableHoursTime( profile.getAvailableHoursTime() );
        profileDto.setBudget( profile.getBudget() );
        profileDto.setCreatedAt( profile.getCreatedAt() );
        profileDto.setId( profile.getId() );
        profileDto.setInterest( profile.getInterest() );
        profileDto.setKnowledgeLevel( profile.getKnowledgeLevel() );
        profileDto.setPlatformPrefered( profile.getPlatformPrefered() );
        profileDto.setUpdatedAt( profile.getUpdatedAt() );

        return profileDto;
    }

    @Override
    public Profile toProfile(SaveProfileDto saveProfileDto) {
        if ( saveProfileDto == null ) {
            return null;
        }

        Profile profile = new Profile();

        profile.setAvailableHoursTime( saveProfileDto.getAvailableHoursTime() );
        profile.setBudget( saveProfileDto.getBudget() );
        profile.setInterest( saveProfileDto.getInterest() );
        profile.setKnowledgeLevel( saveProfileDto.getKnowledgeLevel() );
        profile.setPlatformPrefered( saveProfileDto.getPlatformPrefered() );

        return profile;
    }
}
