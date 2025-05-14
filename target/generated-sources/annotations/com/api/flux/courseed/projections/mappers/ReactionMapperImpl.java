package com.api.flux.courseed.projections.mappers;

import com.api.flux.courseed.persistence.documents.Reaction;
import com.api.flux.courseed.projections.dtos.ReactionDto;
import com.api.flux.courseed.projections.dtos.SaveReactionDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-05T12:22:53-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.z20250331-1358, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class ReactionMapperImpl implements ReactionMapper {

    @Override
    public ReactionDto toReactionDto(Reaction reaction) {
        if ( reaction == null ) {
            return null;
        }

        ReactionDto reactionDto = new ReactionDto();

        reactionDto.setId( reaction.getId() );
        reactionDto.setCreatedAt( reaction.getCreatedAt() );
        reactionDto.setType( reaction.getType() );

        return reactionDto;
    }

    @Override
    public Reaction toReaction(SaveReactionDto saveReactionDto) {
        if ( saveReactionDto == null ) {
            return null;
        }

        Reaction reaction = new Reaction();

        reaction.setCourseId( saveReactionDto.getCourseId() );
        reaction.setType( saveReactionDto.getType() );

        return reaction;
    }
}
