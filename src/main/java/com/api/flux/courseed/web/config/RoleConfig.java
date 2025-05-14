package com.api.flux.courseed.web.config;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.api.flux.courseed.persistence.documents.Role;
import com.api.flux.courseed.persistence.repositories.RoleRepository;
import com.api.flux.courseed.services.interfaces.Roles;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class RoleConfig implements CommandLineRunner {
    
    @Lazy
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        Flux<String> roles = Flux.just(Roles.ADMIN, Roles.USER);

        roles.flatMap(role -> roleRepository.findByName(role)
            .switchIfEmpty(Mono.defer(() -> roleRepository.save(new Role(role))))
        ).then().subscribe();
    }
}