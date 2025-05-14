package com.api.flux.courseed.projections.mappers;

import com.api.flux.courseed.persistence.documents.User;
import com.api.flux.courseed.projections.dtos.CreateUserDto;
import com.api.flux.courseed.projections.dtos.RegisterSubscriptorDto;
import com.api.flux.courseed.projections.dtos.RegisterUserDto;
import com.api.flux.courseed.projections.dtos.UserDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-14T13:00:55-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setId( user.getId() );
        userDto.setEmail( user.getEmail() );
        List<String> list = user.getRoles();
        if ( list != null ) {
            userDto.setRoles( new ArrayList<String>( list ) );
        }
        userDto.setCreatedAt( user.getCreatedAt() );
        userDto.setUpdatedAt( user.getUpdatedAt() );
        userDto.setAcademicLevel( user.getAcademicLevel() );
        userDto.setSex( user.getSex() );
        userDto.setBirthdate( user.getBirthdate() );
        userDto.setImage( user.getImage() );

        return userDto;
    }

    @Override
    public User toUser(RegisterUserDto registerUserDto) {
        if ( registerUserDto == null ) {
            return null;
        }

        User user = new User();

        user.setEmail( registerUserDto.getEmail() );
        user.setPassword( registerUserDto.getPassword() );
        user.setAcademicLevel( registerUserDto.getAcademicLevel() );
        user.setSex( registerUserDto.getSex() );
        user.setBirthdate( registerUserDto.getBirthdate() );

        return user;
    }

    @Override
    public User toUser(CreateUserDto createUserDto) {
        if ( createUserDto == null ) {
            return null;
        }

        User user = new User();

        user.setEmail( createUserDto.getEmail() );
        user.setPassword( createUserDto.getPassword() );
        List<String> list = createUserDto.getRoles();
        if ( list != null ) {
            user.setRoles( new ArrayList<String>( list ) );
        }

        return user;
    }

    @Override
    public User toUser(RegisterSubscriptorDto registerSubscriptorDto) {
        if ( registerSubscriptorDto == null ) {
            return null;
        }

        User user = new User();

        user.setEmail( registerSubscriptorDto.getEmail() );
        user.setPassword( registerSubscriptorDto.getPassword() );

        return user;
    }
}
