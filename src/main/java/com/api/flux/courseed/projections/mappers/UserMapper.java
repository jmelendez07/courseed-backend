package com.api.flux.courseed.projections.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.api.flux.courseed.persistence.documents.User;
import com.api.flux.courseed.projections.dtos.CreateUserDto;
import com.api.flux.courseed.projections.dtos.RegisterSubscriptorDto;
import com.api.flux.courseed.projections.dtos.RegisterUserDto;
import com.api.flux.courseed.projections.dtos.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "reactions", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "prediction", ignore = true)
    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "image", ignore = true)
    User toUser(RegisterUserDto registerUserDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "academicLevel", ignore = true)
    @Mapping(target = "sex", ignore = true)
    @Mapping(target = "birthdate", ignore = true)
    @Mapping(target = "image", ignore = true)
    User toUser(CreateUserDto createUserDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "academicLevel", ignore = true)
    @Mapping(target = "sex", ignore = true)
    @Mapping(target = "birthdate", ignore = true)
    @Mapping(target = "image", ignore = true)
    User toUser(RegisterSubscriptorDto registerSubscriptorDto);
}