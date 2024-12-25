package com.example.backend.controllers;

import com.example.backend.dto.request.LoginRequest;
import com.example.backend.models.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.services.UserService;
import com.example.backend.utils.AuthenticationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Optional;

@RequestMapping("/api/v1/user")
@RestController
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationUtil authenticationUtil;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/sign-up")
    public ResponseEntity<User> signUp(@RequestBody @Valid Optional<User> request) {
        User result = userService.createUser(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/sign-in")
    public ResponseEntity signIn(@RequestBody @Valid LoginRequest request, HttpServletResponse response, HttpServletRequest cookieRequest) throws ParseException {
        String accessToken = authenticationUtil.getTokenFromCookie(cookieRequest);
        if (accessToken == null) {
            if (userService.login(request.getEmail(), request.getPassword()).getBody().getClass()!=String.class) {
                User result = (User) userService.login(request.getEmail(), request.getPassword()).getBody();
                String token = authenticationUtil.generateToken(result.getId());
                authenticationUtil.createCookie(response, token);
            }
            return ResponseEntity.status(HttpStatus.OK).body(userService.login(request.getEmail(), request.getPassword()).getBody());
        }
        if (authenticationUtil.authenticateUser(cookieRequest)!=null) {
            String currentUserId = authenticationUtil.authenticateUser(cookieRequest);
            User result = userRepository.findById(currentUserId).get();
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/sign-out")
    public ResponseEntity<String> signOut(HttpServletResponse response, HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        System.out.println(isAuthenticated);
        if (isAuthenticated!=null) {
            userService.logout(response);
            return ResponseEntity.ok("Log out successfully");
        }
        return ResponseEntity.ok("Something went wrong");
    }

    @GetMapping("/authenticate")
    public ResponseEntity authenticate(HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(isAuthenticated != null ? userRepository.findById(isAuthenticated) : "You are not logged in");
    }

    @GetMapping("/find/{username}")
    public ResponseEntity findUserByUsername(@PathVariable String username, HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByUsername(username));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/find-by-id/{targetId}")
    public ResponseEntity findUserByTargetId(@PathVariable String targetId, HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            User result = userRepository.findById(targetId).get();
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body("Cannot find this user");
    }

    @PutMapping("/update-profile/{userId}")
    public ResponseEntity<User> updateProfile(@PathVariable String userId, @RequestBody @Valid Optional<User> updateRequest, HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            User user = userService.updateUser(userId, isAuthenticated, updateRequest);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/follow/request/{userReceiveId}")
    public ResponseEntity<String> sendFollowRequest(@RequestParam String userId, @PathVariable String userReceiveId, HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            userService.sendFollowRequest(userId, userReceiveId, isAuthenticated);
            return ResponseEntity.ok("Your request is sent");
        }
        return ResponseEntity.badRequest().body("Something went wrong");
    }

    @PostMapping("/follow/accept/{userSendReqId}")
    public ResponseEntity<String> acceptFollowRequest(@RequestParam String userId, @PathVariable String userSendReqId, HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            userService.acceptFollowRequest(userId, userSendReqId, isAuthenticated);
            return ResponseEntity.ok("Congratulations, you two have successfully followed each other");
        }
        return ResponseEntity.badRequest().body("Something went wrong");
    }

    @PostMapping("/follow/deny/{userSendReqId}")
    public ResponseEntity<String> denyFollowRequest(@RequestParam String userId, @PathVariable String userSendReqId, HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            userService.denyFollowRequest(userId, userSendReqId, isAuthenticated);
            return ResponseEntity.ok("Delete request successfully");
        }
        return ResponseEntity.badRequest().body("Something went wrong");
    }

    @PostMapping("/unfollow/{userFollowingId}")
    public ResponseEntity<String> unfollowUser(@RequestParam String userId, @PathVariable String userFollowingId, HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            userService.unfollow(userId, userFollowingId, isAuthenticated);
            return ResponseEntity.ok("Unfollow successfully");
        }
        return ResponseEntity.badRequest().body("Something went wrong");
    }

    @PostMapping("/delete-follower/{followerId}")
    public ResponseEntity<String> deleteFollower(@RequestParam String userId, @PathVariable String followerId, HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            userService.deleteFollower(userId, followerId, isAuthenticated);
            return ResponseEntity.ok("Delete follower successfully");
        }
        return ResponseEntity.badRequest().body("Something went wrong");
    }

    @GetMapping("/get-all")
    public ResponseEntity getAll(@RequestParam int page, @RequestParam int size, HttpServletRequest req) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(req);
        return userService.getAll(isAuthenticated, page, size);
    }

    @GetMapping("/delete")
    public ResponseEntity delete(@RequestParam String userId,  HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        return userService.delete(userId, isAuthenticated);
    }
}
