package com.example.backend.mapper;

import com.example.backend.models.Post;
import org.mapstruct.Mapper;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface PostMapper {
    default Post optionalToPost(Optional<Post> request) {
        return request.orElse(null);
    }
}
