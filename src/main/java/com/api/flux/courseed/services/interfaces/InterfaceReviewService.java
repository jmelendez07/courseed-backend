package com.api.flux.courseed.services.interfaces;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;

import com.api.flux.courseed.projections.dtos.CourseAverageRating;
import com.api.flux.courseed.projections.dtos.CreateReviewDto;
import com.api.flux.courseed.projections.dtos.ReviewCountByMonth;
import com.api.flux.courseed.projections.dtos.ReviewDto;
import com.api.flux.courseed.projections.dtos.TotalReviewsDto;
import com.api.flux.courseed.projections.dtos.UpdateReviewDto;
import reactor.core.publisher.Mono;

public interface InterfaceReviewService {
    Mono<TotalReviewsDto> getTotalReviews();
    Mono<List<CourseAverageRating>> getTotalNegativeReviews();
    Mono<Page<ReviewDto>> getAllReviews(int page, int size, String search, String userId);
    Mono<Page<ReviewDto>> getReviewsByCourseId(String courseId, int page, int size);
    Mono<Page<ReviewDto>> getReviewsByAuthUser(Principal principal, int page, int size, String search);
    Mono<List<ReviewCountByMonth>> getReviewCountsForLastSixMonths();
    Mono<Object> createReview(Principal principal, CreateReviewDto createReviewDto);
    Mono<ReviewDto> updateReview(Principal principal, String id, UpdateReviewDto saveReviewDto);
    Mono<Boolean> deleteReview(Principal principal, String id);
    Mono<Integer> getTotalReviewsBySuscriptor(Principal principal);
}
