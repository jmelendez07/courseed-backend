package com.api.flux.courseed.services.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;

import com.api.flux.courseed.projections.dtos.CreateUserDto;
import com.api.flux.courseed.projections.dtos.TotalUsersDto;
import com.api.flux.courseed.projections.dtos.UpdateUserEmailDto;
import com.api.flux.courseed.projections.dtos.UpdateUserPasswordDto;
import com.api.flux.courseed.projections.dtos.UpdateUserRolesDto;
import com.api.flux.courseed.projections.dtos.UserCountByMonth;
import com.api.flux.courseed.projections.dtos.UserDto;

import reactor.core.publisher.Mono;

public interface InterfaceUserService {
    Mono<TotalUsersDto> getTotalUsers();
    Mono<Integer> getAllUsersCountByInterestOrModality(String interest, String modality);
    Mono<Page<UserDto>> getAllUsers(int page, int size);
    Mono<Integer> getAllUsersCount();
    Mono<UserDto> getUserById(String id);
    Mono<UserDto> getUserByEmail(String email);
    Mono<List<UserCountByMonth>> getUserCountForLastSixMonths();
    Mono<Object> createUser(CreateUserDto createUserDto);
    Mono<Object> updateUserEmail(String id, UpdateUserEmailDto updateUserEmailDto);
    Mono<UserDto> updateUserPassword(String id, UpdateUserPasswordDto updateUserPasswordDto);
    Mono<UserDto> updateUserRoles(String id, UpdateUserRolesDto updateUserRolesDto);
    Mono<Boolean> deleteUser(String id);
}
