package com.api.flux.courseed.persistence.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import com.api.flux.courseed.persistence.documents.Profile;
import reactor.core.publisher.Mono;

@Repository
public interface ProfileRepository extends ReactiveMongoRepository<Profile, String> {
    Mono<Profile> findByUserId(String userId);
}
