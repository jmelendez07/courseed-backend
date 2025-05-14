package com.api.flux.courseed.projections.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.api.flux.courseed.persistence.documents.Review;
import com.api.flux.courseed.projections.dtos.CreateReviewDto;
import com.api.flux.courseed.projections.dtos.ReviewDto;
import com.api.flux.courseed.projections.dtos.UpdateReviewDto;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "course", ignore = true)
    ReviewDto toReviewDto(Review review); 

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "userId", ignore = true)
    Review toReview(CreateReviewDto createReviewDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courseId", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "userId", ignore = true)
    Review toReview(UpdateReviewDto updateReviewDto);
}
