package com.example.backend.controllers;

import com.example.backend.models.Community;
import com.example.backend.models.Post;
import com.example.backend.services.CommunityService;
import com.example.backend.utils.AuthenticationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Objects;

@RequestMapping("/api/v1/community")
@RestController
@CrossOrigin
public class CommunityController {
    @Autowired
    private CommunityService communityService;
    @Autowired
    private AuthenticationUtil authenticationUtil;

    @PostMapping("/create-community")
    public ResponseEntity establish(@RequestBody @Valid Community communityRequest, HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            Community community = communityService.createCommunity(communityRequest, isAuthenticated);
            return ResponseEntity.ok().body(community);
        }
        return ResponseEntity.badRequest().body("Please login before create a new community");
    }

    @GetMapping("/{communityId}")
    public ResponseEntity getCommunity(@PathVariable String communityId, HttpServletRequest request) throws ParseException {
        if (communityId==null) {
            return ResponseEntity.badRequest().body("This community is not available");
        }
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            return ResponseEntity.ok().body(communityService.getDetailCommunity(communityId, isAuthenticated));
        }
        return ResponseEntity.badRequest().body("Please login before retrieve a community");
    }

    @GetMapping("/top-community")
    public ResponseEntity getTop30(HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            return ResponseEntity.ok().body(communityService.getTopCommunities(isAuthenticated));
        }
        return ResponseEntity.badRequest().body("Please login before see this feature");
    }

    @GetMapping("/my-communities")
    public ResponseEntity getMyCommunities(HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            return ResponseEntity.ok().body(communityService.getMyCommunities(isAuthenticated));
        }
        return ResponseEntity.badRequest().body("Please login before see this feature");
    }

    @GetMapping("/feed-for-my-communities")
    public ResponseEntity getFeedForMyCommunities(HttpServletRequest request, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "0") int pageSize) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            return ResponseEntity.ok().body(communityService.getFeedForMyCommunities(isAuthenticated, page, pageSize));
        }
        return ResponseEntity.badRequest().body("Please login before see this feature");
    }

    @GetMapping("/post-in-community/{communityId}")
    public ResponseEntity getPostForDetailCommunity(HttpServletRequest request, @PathVariable String communityId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "0") int pageSize) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            return ResponseEntity.ok().body(communityService.getPostInCommunity(isAuthenticated, communityId, page, pageSize));
        }
        return ResponseEntity.badRequest().body("Please login before see this feature");
    }

    @GetMapping("/feed")
    public ResponseEntity getFeed(HttpServletRequest request, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "0") int pageSize) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            return ResponseEntity.ok().body(communityService.getFeed(isAuthenticated, page, pageSize));
        }
        return ResponseEntity.badRequest().body("Please login before see this feature");
    }

    @PostMapping("/join-community/{communityId}")
    public ResponseEntity getJoinCommunity(HttpServletRequest request, @PathVariable String communityId) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            return ResponseEntity.ok().body(communityService.sendRequest(isAuthenticated, communityId));
        }
        return ResponseEntity.badRequest().body("Please login before see this feature");
    }

    @PostMapping("/leave-community/{communityId}")
    public ResponseEntity leaveCommunity(HttpServletRequest request, @PathVariable String communityId) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            return ResponseEntity.ok().body(communityService.leaveCommunity(isAuthenticated, communityId));
        }
        return ResponseEntity.badRequest().body("Please login before see this feature");
    }

    @PostMapping("/interact-request/{communityId}")
    public ResponseEntity interactRequest(@PathVariable String communityId, @RequestParam String pendingId, @RequestParam String type, HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated==null) {
            return ResponseEntity.badRequest().body("Please login before see this feature");
        }
        if (Objects.equals(pendingId, "") || Objects.equals(type, "")) {
            return ResponseEntity.badRequest().body("Please fill full property");
        }
        return ResponseEntity.ok().body(communityService.interactRequest(isAuthenticated, pendingId, communityId, type));
    }

    @PostMapping("/upload-post/{communityId}")
    public ResponseEntity uploadPostToCommunity(HttpServletRequest request, @PathVariable String communityId, @RequestBody @Valid Post post) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated==null) {
            return ResponseEntity.badRequest().body("Please login before see this feature");
        }
        if (post==null) {
            return ResponseEntity.badRequest().body("Please fill full property");
        }
        return ResponseEntity.ok().body(communityService.uploadPost(isAuthenticated, communityId, post));
    }

    @GetMapping("/get-pending-post")
    public ResponseEntity getPendingPost(@RequestParam String communityId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "0") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(communityService.getPendingPost(communityId, page, size));
    }

    @GetMapping("/confirm-post")
    public ResponseEntity confirmPost(@RequestParam String postId) {
        return communityService.confirmPost(postId);
    }
}
