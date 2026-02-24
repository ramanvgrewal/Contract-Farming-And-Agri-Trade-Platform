package com.agricontract.auth.mapper;

import com.agricontract.auth.dto.RegisterRequest;
import com.agricontract.auth.dto.UserDto;
import com.agricontract.auth.entity.User;
import com.agricontract.auth.entity.UserProfile;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-24T11:49:50+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(RegisterRequest request) {
        if ( request == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.email( request.getEmail() );
        user.role( request.getRole() );

        return user.build();
    }

    @Override
    public UserProfile toProfileEntity(RegisterRequest request) {
        if ( request == null ) {
            return null;
        }

        UserProfile.UserProfileBuilder userProfile = UserProfile.builder();

        userProfile.name( request.getName() );
        userProfile.phone( request.getPhone() );
        userProfile.address( request.getAddress() );
        userProfile.city( request.getCity() );
        userProfile.state( request.getState() );

        return userProfile.build();
    }

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.name( userProfileName( user ) );
        userDto.phone( userProfilePhone( user ) );
        userDto.address( userProfileAddress( user ) );
        userDto.city( userProfileCity( user ) );
        userDto.state( userProfileState( user ) );
        userDto.id( user.getId() );
        userDto.email( user.getEmail() );
        userDto.role( user.getRole() );

        return userDto.build();
    }

    private String userProfileName(User user) {
        if ( user == null ) {
            return null;
        }
        UserProfile profile = user.getProfile();
        if ( profile == null ) {
            return null;
        }
        String name = profile.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String userProfilePhone(User user) {
        if ( user == null ) {
            return null;
        }
        UserProfile profile = user.getProfile();
        if ( profile == null ) {
            return null;
        }
        String phone = profile.getPhone();
        if ( phone == null ) {
            return null;
        }
        return phone;
    }

    private String userProfileAddress(User user) {
        if ( user == null ) {
            return null;
        }
        UserProfile profile = user.getProfile();
        if ( profile == null ) {
            return null;
        }
        String address = profile.getAddress();
        if ( address == null ) {
            return null;
        }
        return address;
    }

    private String userProfileCity(User user) {
        if ( user == null ) {
            return null;
        }
        UserProfile profile = user.getProfile();
        if ( profile == null ) {
            return null;
        }
        String city = profile.getCity();
        if ( city == null ) {
            return null;
        }
        return city;
    }

    private String userProfileState(User user) {
        if ( user == null ) {
            return null;
        }
        UserProfile profile = user.getProfile();
        if ( profile == null ) {
            return null;
        }
        String state = profile.getState();
        if ( state == null ) {
            return null;
        }
        return state;
    }
}
