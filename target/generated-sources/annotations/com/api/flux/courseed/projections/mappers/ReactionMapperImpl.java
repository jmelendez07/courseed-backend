package com.api.flux.courseed.projections.mappers;

import com.api.flux.courseed.persistence.documents.Reaction;
import com.api.flux.courseed.projections.dtos.ReactionDto;
import com.api.flux.courseed.projections.dtos.SaveReactionDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-14T13:00:55-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Eclipse Adoptium)"
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
