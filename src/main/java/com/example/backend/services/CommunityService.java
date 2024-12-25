package com.example.backend.services;

import com.example.backend.constant.MessageCode;
import com.example.backend.mapper.PostMapper;
import com.example.backend.models.BelongsTo;
import com.example.backend.models.Community;
import com.example.backend.models.Post;
import com.example.backend.models.User;
import com.example.backend.repository.CommunityRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.response.FeedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Optionals;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.backend.constant.MessageCode.REPORT_00;
import static com.example.backend.constant.MessageCode.REPORT_01;

@Service
public class CommunityService {
    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;

    public Community createCommunity(Community community, String currentUserId) {
        if (userRepository.findById(currentUserId).isEmpty()) {
            throw new IllegalArgumentException("You must login first");
        }
        User user = userRepository.findById(currentUserId).get();
        community.setFounder(user);
        community.getMembers().add(user);
        return communityRepository.save(community);
    }

    public Community getDetailCommunity(String communityId, String currentUserId) {
        if (userRepository.findById(currentUserId).isEmpty()) {
            throw new IllegalArgumentException("You must login first");
        }
        if (communityRepository.findById(communityId).isEmpty()) {
            throw new IllegalArgumentException("This community does not exist");
        }
        return communityRepository.findById(communityId).get();
    }

    public List<Community> getTopCommunities(String currentUserId) {
        if (userRepository.findById(currentUserId).isEmpty()) {
            throw new IllegalArgumentException("You must login first");
        }
        List<Community> allCommunities = communityRepository.findAll();
        return allCommunities.stream().sorted(Comparator.comparingInt((Community c)->c.getMembers().size()).reversed()).limit(30).collect(Collectors.toList());
    }

    public List<Community> getMyCommunities(String currentUserId) {
        if (userRepository.findById(currentUserId).isEmpty()) {
            throw new IllegalArgumentException("You must login first");
        }
        return communityRepository.getCommunitiesByMembersContainingOrderByCreatedAtDesc(userRepository.findById(currentUserId).get());
    }

    public FeedResponse getFeedForMyCommunities(String currentUserId, int page, int pageSize) {
        if (userRepository.findById(currentUserId).isEmpty()) {
            throw new IllegalArgumentException("You must login first");
        }
        List<Community> communities = getMyCommunities(currentUserId);

        List<String> ids = communities.stream().map(Community::getId).toList();

        Pageable pageable = PageRequest.of(page, pageSize);
        long totalSize = postRepository.countPostsByBelongsTo_IdAndPendingFalseOrBelongsTo_IdAndPendingNull(ids, ids);
        return new FeedResponse(postRepository.findPostsByBelongsTo_IdAndPendingFalseOrBelongsTo_IdAndPendingNullOrderByCreatedAtDesc(ids, ids, pageable), totalSize);
    }

    public FeedResponse getFeed(String currentUserId, int page, int pageSize) {
        if (userRepository.findById(currentUserId).isEmpty()) {
            throw new IllegalArgumentException("You must login first");
        }
        Pageable pageable = PageRequest.of(page, pageSize);
        long totalSize= postRepository.countPostsByBelongsToMembersNotContaining(currentUserId);
        return new FeedResponse(postRepository.findPostsByBelongsToMembersNotContaining(currentUserId, pageable), totalSize);
    }

    public FeedResponse getPostInCommunity(String currentUserId, String communityId, int page, int pageSize) {
        if (userRepository.findById(currentUserId).isEmpty()) {
            throw new IllegalArgumentException("You must login first");
        }
        if (communityRepository.findById(communityId).isEmpty()) {
            throw new IllegalArgumentException("This community does not exist");
        }
        Pageable pageable = PageRequest.of(page, pageSize);
        long totalSize = postRepository.countPostsByBelongsTo_IdAndPendingFalseOrPendingNullAndBelongsTo_IdOrderByCreatedAt(communityId, communityId);
        return new FeedResponse(postRepository.findPostsByBelongsTo_IdAndPendingFalseOrPendingNullAndBelongsTo_IdOrderByCreatedAtDesc(communityId, communityId, pageable), totalSize);
    }

    public FeedResponse getPendingPost(String communityId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new FeedResponse(postRepository.findPostsByBelongsTo_IdAndPendingTrueOrderByCreatedAtDesc(communityId, pageable), postRepository.countPostsByBelongsTo_IdAndPendingTrue(communityId));
    }

    public ResponseEntity confirmPost(String postId) {
        Post post = postRepository.findPostById(postId);
        if (post!=null) {
            post.setPending(false);
            postRepository.save(post);
            return ResponseEntity.ok(post);
        }
        return ResponseEntity.badRequest().body("Cannot find this post");
    }

    public MessageCode sendRequest(String currentUserId, String idCommunity) {
        if (userRepository.findById(currentUserId).isEmpty()) {
            throw new IllegalArgumentException("You must login first");
        }
        if (communityRepository.findById(idCommunity).isEmpty()) {
            throw new IllegalArgumentException("This community does not exist");
        }
        Community community = communityRepository.findById(idCommunity).get();
        User user = userRepository.findById(currentUserId).get();
        if (community.getPendingRequests().contains(user)) {
            community.getPendingRequests().remove(user);
            communityRepository.save(community);
            return MessageCode.REPORT_03;
        } else {
            community.getPendingRequests().add(user);
            communityRepository.save(community);
        }
        return REPORT_00;
    }

    public MessageCode leaveCommunity(String currentUserId, String communityId) {
        if (userRepository.findById(currentUserId).isEmpty()) {
            throw new IllegalArgumentException("You must login first");
        }
        if (communityRepository.findById(communityId).isEmpty()) {
            throw new IllegalArgumentException("This community does not exist");
        }
        Community community = communityRepository.findById(communityId).get();
        User user = userRepository.findById(currentUserId).get();
        if (community.getMembers().stream().toList().contains(user)){
            community.getMembers().remove(user);
            communityRepository.save(community);
            return REPORT_00;
        }
        return REPORT_01;
    }

    public String interactRequest(String currentUserId, String pendingUserId, String idCommunity, String type) {
        if (userRepository.findById(currentUserId).isEmpty()) {
            throw new IllegalArgumentException("You must login first");
        }
        if (userRepository.findById(pendingUserId).isEmpty()) {
            throw new IllegalArgumentException("This user haven't sent a pending request");
        }
        if (communityRepository.findById(idCommunity).isEmpty()) {
            throw new IllegalArgumentException("This community does not exist");
        }
        Community community = communityRepository.findById(idCommunity).get();
        User user = userRepository.findById(pendingUserId).get();
        if (community.getPendingRequests().contains(user) && community.getFounder().equals(userRepository.findById(currentUserId).get())) {
            if (Objects.equals(type, "deny")) {
                community.getPendingRequests().remove(user);
                communityRepository.save(community);
                return "Join request of " + user.getUsername() + " has been rejected";
            } else if (Objects.equals(type, "accept")) {
                community.getPendingRequests().remove(user);
                community.getMembers().add(user);
                communityRepository.save(community);
                return user.getUsername() + " has been joined our community";
            }
        }
        return "You are not allowed to interact with this community";
    }

    public MessageCode uploadPost(String currentUserId, String idCommunity, Post post) {
        if (userRepository.findById(currentUserId).isEmpty()) {
            throw new IllegalArgumentException("You must login first");
        }
        if (communityRepository.findById(idCommunity).isEmpty()) {
            throw new IllegalArgumentException("This community does not exist");
        }
        if (post==null) {
            throw new IllegalArgumentException("This post does not exist");
        }
        Community community = communityRepository.findById(idCommunity).get();
        User user = userRepository.findById(currentUserId).get();
        post.setBelongsTo(community);
        post.setPending(true);
        Optional<Post> postOptional = Optional.of(post);
        if (community.getMembers().contains(user) || community.getFounder().equals(user)) {
            postService.createPost(postOptional, currentUserId);
        }
        return REPORT_00;
    }

}
