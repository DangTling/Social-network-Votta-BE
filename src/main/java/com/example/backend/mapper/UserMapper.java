package com.example.backend.mapper;

import com.example.backend.models.User;
import org.mapstruct.Mapper;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(User request);
    default User optionalToUser(Optional<User> request) {
        return request.orElse(null);
    }
}
