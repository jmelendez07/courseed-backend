package com.api.flux.courseed.persistence.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.api.flux.courseed.persistence.documents.Role;

import reactor.core.publisher.Mono;

@Repository
public interface RoleRepository extends ReactiveMongoRepository<Role, String> {
    Mono<Role> findByName(String name);
}
