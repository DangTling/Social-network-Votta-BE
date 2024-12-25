package com.example.backend.services;

import com.example.backend.mapper.UserMapper;
import com.example.backend.models.User;
import com.example.backend.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public ResponseEntity getUserByUsername(String username) {
        String regex = ".*"+ Pattern.quote(username)+".*";
        if(userRepository.findByUsernameRegex(regex).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not existing user with username or name "+username);
        }
        return ResponseEntity.status(HttpStatus.OK).body(userRepository.findByUsernameRegex(regex));
    }

    public User getUserByEmail(String email) {
        Optional<User> userNeedFind = userRepository.getUserByEmail(email);
        return userMapper.optionalToUser(userNeedFind);
    }

    public User createUser(Optional<User> request) {
        if(userRepository.getUserByUsername(request.get().getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.getUserByEmail(request.get().getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        User newUser = userMapper.optionalToUser(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        newUser.setPassword(passwordEncoder.encode(request.get().getPassword()));
        return userRepository.save(newUser);
    }

    public User updateUser(String userId, String currentUserId, Optional<User> request) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        if (!Objects.equals(currentUserId, userId)) {
            throw new IllegalArgumentException("You don't have this permission");
        }
        User currentUser = userRepository.findById(userId).get();
        if(userRepository.getUserByUsername(request.get().getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        User newUser = userMapper.optionalToUser(request);
        newUser.setId(currentUserId);
        newUser.setEmail(currentUser.getEmail());
        newUser.setName(currentUser.getName());
        newUser.setFollower(currentUser.getFollower());
        newUser.setFollowing(currentUser.getFollowing());
        newUser.setFollowRequests(currentUser.getFollowRequests());
        newUser.setDob(currentUser.getDob());
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        newUser.setPassword(passwordEncoder.encode(request.get().getPassword()));
        return userRepository.save(newUser);
    }

    public ResponseEntity login(String email, String password) {
        User userMatch = getUserByEmail(email);

        if (userMatch == null) {
            return ResponseEntity.status(HttpStatus.OK).body("User with this email isn't existing in server");
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        if (!passwordEncoder.matches(password, userMatch.getPassword())) {
            return ResponseEntity.status(HttpStatus.OK).body("Your password is not matching with data from my server");
        }
        return ResponseEntity.status(HttpStatus.OK).body(userMatch);
    }

    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("accessToken", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    public void sendFollowRequest(String userId, String userReceiveId, String currentUserId) {
        if(userRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("You are not logged in");
        }
        if (userRepository.findById(userReceiveId).isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        if (!Objects.equals(currentUserId, userId)) {
            throw new IllegalArgumentException("You don't have this permission");
        }
        if (userRepository.findById(userId).get().getFollowing() != null && userRepository.findById(userId).get().getFollowing().contains(userReceiveId)) {
            throw new IllegalArgumentException("You are already following this user");
        }
        if (userRepository.findById(userReceiveId).get().getFollowRequests() != null && userRepository.findById(userReceiveId).get().getFollowRequests().contains(userId)) {
            throw new IllegalArgumentException("Following request is already sent");
        }
        User userReceiveRequest = userRepository.findById(userReceiveId).get();
        userReceiveRequest.getFollowRequests().add(userId);
        userRepository.save(userReceiveRequest);
    }

    public void acceptFollowRequest(String userId, String userSendReqId, String currentUserId) {
        if(userRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("You are not logged in");
        }
        if (userRepository.findById(userSendReqId).isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        if (!Objects.equals(currentUserId, userId)) {
            throw new IllegalArgumentException("You don't have this permission");
        }
        User userSendRequest = userRepository.findById(userSendReqId).get();
        User user = userRepository.findById(userId).get();
        if (!user.getFollowRequests().contains(userSendReqId)) {
            throw new IllegalArgumentException("Following request is never sent");
        }
        user.getFollowRequests().remove(userSendReqId);
        user.getFollower().add(userSendReqId);
        userSendRequest.getFollowing().add(userId);
        userRepository.save(userSendRequest);
        userRepository.save(user);
    }

    public void denyFollowRequest(String userId, String userSendReqId, String currentUserId) {
        if(userRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("You are not logged in");
        }
        if (userRepository.findById(userSendReqId).isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        if (!Objects.equals(currentUserId, userId)) {
            throw new IllegalArgumentException("You don't have this permission");
        }
        User user = userRepository.findById(userId).get();
        if (!user.getFollowRequests().contains(userSendReqId)) {
            throw new IllegalArgumentException("Following request is never sent");
        }
        user.getFollowRequests().remove(userSendReqId);
        userRepository.save(user);
    }

    public void unfollow(String userId, String followingId, String currentUserId) {
        if(userRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("You are not logged in");
        }
        if (userRepository.findById(followingId).isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        if (!Objects.equals(currentUserId, userId)) {
            throw new IllegalArgumentException("You don't have this permission");
        }
        User user = userRepository.findById(userId).get();
        User followingUser = userRepository.findById(followingId).get();
        if (!user.getFollowing().contains(followingId)) {
            throw new IllegalArgumentException("You are not following this user");
        }
        user.getFollowing().remove(followingId);
        followingUser.getFollower().remove(userId);
        userRepository.save(followingUser);
        userRepository.save(user);
    }

    public void deleteFollower(String userId, String followerId, String currentUserId) {
        if(userRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("You are not logged in");
        }
        if (userRepository.findById(followerId).isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        if (!Objects.equals(currentUserId, userId)) {
            throw new IllegalArgumentException("You don't have this permission");
        }
        User user = userRepository.findById(userId).get();
        User follower = userRepository.findById(followerId).get();
        if (!user.getFollower().contains(followerId)) {
            throw new IllegalArgumentException("This user is not following you");
        }
        user.getFollower().remove(followerId);
        follower.getFollowing().remove(userId);
        userRepository.save(follower);
        userRepository.save(user);
    }

    public ResponseEntity getAll(String adminId, int page, int size) {
        User admin = userRepository.findById(adminId).orElse(null);
        Pageable pageable = PageRequest.of(page, size);
        if (Objects.equals(admin.getRole(), "admin")) {
            return ResponseEntity.status(HttpStatus.OK).body(userRepository.findAll(pageable));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You don't have permission");
    }

    public ResponseEntity delete(String userId, String adminId) {
        User admin = userRepository.findById(adminId).orElse(null);
        if (Objects.equals(admin.getRole(), "admin")) {
            userRepository.deleteById(userId);
            return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You don't have permission");
    }
}
