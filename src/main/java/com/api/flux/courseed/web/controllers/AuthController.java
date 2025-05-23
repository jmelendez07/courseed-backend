package com.api.flux.courseed.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.api.flux.courseed.projections.dtos.LoginUserDto;
import com.api.flux.courseed.projections.dtos.RegisterSubscriptorDto;
import com.api.flux.courseed.projections.dtos.RegisterUserDto;
import com.api.flux.courseed.projections.dtos.UpdateAuthPasswordDto;
import com.api.flux.courseed.projections.dtos.UpdateProfileDto;
import com.api.flux.courseed.services.implementations.AuthService;
import com.api.flux.courseed.services.implementations.ValidationService;

import reactor.core.publisher.Mono;

public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ValidationService validationService;

    @Value("${spring.webflux.base-path:}")
    private String basePath;

    public Mono<ServerResponse> getAuthUser(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> authService.getAuthUser(principal)
                .flatMap(userDto -> ServerResponse.ok().bodyValue(userDto))
            )
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoginUserDto.class)
            .doOnNext(validationService::validate)
            .flatMap(loginUserDto -> authService.login(loginUserDto)
                .flatMap(tokenDto -> ServerResponse.ok().bodyValue(tokenDto))
                .switchIfEmpty(ServerResponse.notFound().build())
            );
    }

    public Mono<ServerResponse> register(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(RegisterUserDto.class)
            .doOnNext(validationService::validate)
            .flatMap(registerUserDto -> authService.register(registerUserDto)
                .flatMap(userDto -> ServerResponse.ok().bodyValue(userDto))
                .switchIfEmpty(ServerResponse.notFound().build())
            );
    }

    public Mono<ServerResponse> registerSubscriptor(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(RegisterSubscriptorDto.class)
            .doOnNext(validationService::validate)
            .flatMap(registerSubscriptorDto -> authService.registerSubscriptor(registerSubscriptorDto)
                .flatMap(tokenDto -> ServerResponse.ok().bodyValue(tokenDto))
                .switchIfEmpty(ServerResponse.notFound().build())
            );
    }

    public Mono<ServerResponse> updatePassword(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UpdateAuthPasswordDto.class)
            .doOnNext(validationService::validate)
            .flatMap(updateAuthPasswordDto -> serverRequest.principal()
                .flatMap(principal -> authService.updatePassword(principal, updateAuthPasswordDto)
                    .flatMap(tokenDto -> ServerResponse.ok().bodyValue(tokenDto))
                    .switchIfEmpty(ServerResponse.notFound().build())
                )
            );
    }

    public Mono<ServerResponse> updateProfile(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UpdateProfileDto.class)
            .doOnNext(validationService::validate)
            .flatMap(updateProfileDto -> serverRequest.principal()
                .flatMap(principal -> authService.updateProfile(principal, updateProfileDto)
                    .flatMap(userDto -> ServerResponse.ok().bodyValue(userDto))
                    .switchIfEmpty(ServerResponse.notFound().build())
                )
            );
    } 

    public Mono<ServerResponse> subscribe(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> authService.subscribe(principal)
                .flatMap(tokenDto -> ServerResponse.ok().bodyValue(tokenDto))
                .switchIfEmpty(ServerResponse.notFound().build())
            );
    }

    public Mono<ServerResponse> uploadAvatar(ServerRequest serverRequest) 
    {
        return serverRequest.multipartData()
            .flatMap(parts -> {
                FilePart imagePart = (FilePart) parts.getFirst("image");
                
                String baseUrl = serverRequest.uri().getScheme() + "://" + serverRequest.uri().getHost() + ":" + serverRequest.uri().getPort() +
                    (basePath != null && !basePath.isBlank() ? basePath : "");

                if (imagePart == null || imagePart.filename() == null || imagePart.filename().isBlank()) {
                    return ServerResponse.badRequest().bodyValue("Para proceder, debes completar el campo correspondiente a la imagen.");
                }

                if (!this.isValidImage(imagePart)) {
                    return ServerResponse.badRequest().bodyValue("La imagen debe ser de tipo válido (jpg, png, jpeg) y menor a 2 MB.");
                }

                return serverRequest.principal()
                    .flatMap(principal -> authService.updloadAvatar(principal, imagePart, baseUrl)
                        .flatMap(userDto -> ServerResponse.ok().bodyValue(userDto))
                        .switchIfEmpty(ServerResponse.notFound().build())
                    );
            });
    }
    
    public boolean isValidImage(FilePart image) {
        if (image == null) {
            return false;
        }

        MediaType mediaType = image.headers().getContentType();

        if (mediaType == null) {
            return false;
        }

        if (!mediaType.toString().startsWith("image/")) {
            return false;
        }

        final long MAX_SIZE = 2 * 1024 * 1024;
        return image.headers().getContentLength() <= MAX_SIZE;
    }
}