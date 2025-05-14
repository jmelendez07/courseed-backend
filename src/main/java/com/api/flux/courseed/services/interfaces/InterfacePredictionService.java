package com.api.flux.courseed.services.interfaces;

import com.api.flux.courseed.persistence.documents.UserCourseRecomended;

import reactor.core.publisher.Mono;

public interface InterfacePredictionService {
    Mono<UserCourseRecomended> getUserCourseRecomended(String userId, String courseId);
}
