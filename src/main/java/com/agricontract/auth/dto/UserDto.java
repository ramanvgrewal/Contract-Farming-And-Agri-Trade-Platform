package com.agricontract.auth.dto;

import com.agricontract.auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private UUID id;
    private String email;
    private Role role;
    private String name;
    private String phone;
    private String address;
    private String city;
    private String state;
}
