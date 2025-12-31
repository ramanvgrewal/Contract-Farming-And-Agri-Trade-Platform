package com.agricontract.auth.mapper;

import com.agricontract.auth.dto.RegisterRequest;
import com.agricontract.auth.dto.UserDto;
import com.agricontract.auth.entity.User;
import com.agricontract.auth.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "profile", ignore = true)
    User toEntity(RegisterRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "city", source = "city")
    @Mapping(target = "state", source = "state")
    UserProfile toProfileEntity(RegisterRequest request);

    @Mapping(target = "name", source = "profile.name")
    @Mapping(target = "phone", source = "profile.phone")
    @Mapping(target = "address", source = "profile.address")
    @Mapping(target = "city", source = "profile.city")
    @Mapping(target = "state", source = "profile.state")
    UserDto toDto(User user);
}
