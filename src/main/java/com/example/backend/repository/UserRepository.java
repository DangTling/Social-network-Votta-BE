package com.example.backend.repository;

import com.example.backend.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByUsername(String username);

    @Query("{ $or: [ { 'username': { $regex: ?0, $options: 'i' } }, { 'name': { $regex: ?0, $options: 'i' } } ] }")
    List<User> findByUsernameRegex(String username);
}
