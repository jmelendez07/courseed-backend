package com.api.flux.courseed.projections.mappers;

import com.api.flux.courseed.persistence.documents.Review;
import com.api.flux.courseed.projections.dtos.CreateReviewDto;
import com.api.flux.courseed.projections.dtos.ReviewDto;
import com.api.flux.courseed.projections.dtos.UpdateReviewDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-14T13:00:55-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Eclipse Adoptium)"
)
@Component
public class ReviewMapperImpl implements ReviewMapper {

    @Override
    public ReviewDto toReviewDto(Review review) {
        if ( review == null ) {
            return null;
        }

        ReviewDto reviewDto = new ReviewDto();

        reviewDto.setId( review.getId() );
        reviewDto.setContent( review.getContent() );
        reviewDto.setRating( review.getRating() );
        reviewDto.setCreatedAt( review.getCreatedAt() );
        reviewDto.setUpdatedAt( review.getUpdatedAt() );

        return reviewDto;
    }

    @Override
    public Review toReview(CreateReviewDto createReviewDto) {
        if ( createReviewDto == null ) {
            return null;
        }

        Review review = new Review();

        review.setCourseId( createReviewDto.getCourseId() );
        review.setContent( createReviewDto.getContent() );
        review.setRating( createReviewDto.getRating() );

        return review;
    }

    @Override
    public Review toReview(UpdateReviewDto updateReviewDto) {
        if ( updateReviewDto == null ) {
            return null;
        }

        Review review = new Review();

        review.setContent( updateReviewDto.getContent() );
        review.setRating( updateReviewDto.getRating() );

        return review;
    }
}
