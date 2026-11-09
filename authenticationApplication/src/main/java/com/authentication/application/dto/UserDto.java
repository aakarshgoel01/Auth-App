package com.authentication.application.dto;

import com.authentication.application.entity.Provider;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserDto {
    private UUID id ;
    private String name;
    private String password;
    private String email;
    private String image;
    private boolean enable;
    private Instant createdAt;
    private Instant updatedAt;
    private Provider provider = Provider.LOCAL;
    private Set<RoleDto> roles = new HashSet<RoleDto>();
}
