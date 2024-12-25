package com.example.backend.services;

import com.example.backend.mapper.PostMapper;
import com.example.backend.models.Post;
import com.example.backend.models.Reply;
import com.example.backend.models.User;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.response.FeedResponse;
import com.example.backend.response.TopCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@EnableMongoAuditing
@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserRepository userRepository;

    public Post createPost(Optional<Post> request, String currentUserId) {
        Post newPost = postMapper.optionalToPost(request);
        User currentUser = userRepository.findById(currentUserId).get();
        newPost.setPostedBy(currentUser);
        return postRepository.save(newPost);
    }

    public Post editPost(String currentUserId, String postId, Post newPost) {
        if (postRepository.findById(postId).isEmpty()) {
            throw new IllegalArgumentException("Post not found");
        }
        if (userRepository.findById(currentUserId).isEmpty()) {
            throw new IllegalArgumentException("You should login first");
        }

        Post currentPost = postRepository.findById(postId).get();
        if (!currentUserId.equals(currentPost.getPostedBy().getId())) {
            throw new IllegalArgumentException("You are not allowed to edit this post");
        }

        currentPost.setPostPic(newPost.getPostPic());
        currentPost.setCaption(newPost.getCaption());
        currentPost.setTags(newPost.getTags());

        return postRepository.save(currentPost);

    }

    public Post savedPost(String currentUserId, String postId) {
        if (postRepository.findById(postId).isEmpty()) {
            throw new IllegalArgumentException("Post not found");
        }
        Post currentPost = postRepository.findById(postId).get();
        User currentUser = userRepository.findById(currentUserId).get();
        if (currentUserId.equals(currentPost.getPostedBy().getId())) {
            throw new IllegalArgumentException("You don't need to save your post");
        }
        if (!currentPost.getSavedIn().contains(currentUser)) {
            currentPost.getSavedIn().add(currentUser);
        } else {
            currentPost.getSavedIn().remove(currentUser);
        }
        return postRepository.save(currentPost);
    }

    public FeedResponse getSavedPosts(String currentUserId, int page, int pageSize) {
        if (userRepository.findById(currentUserId).isEmpty()) {
            throw new IllegalArgumentException("You should login first");
        }
        Pageable pageable = PageRequest.of(page, pageSize);
        return new FeedResponse(postRepository.findPostsBySavedInContainsOrderByCreatedAt(currentUserId, pageable), postRepository.countPostsBySavedInContains(currentUserId));
    }

    public List<Post> getAllPosts(String currentUserId, String targetUserId) {
        if (!userRepository.existsById(targetUserId)) {
            throw new IllegalArgumentException("This account isn't exited");
        }
        User targetUser = userRepository.findById(targetUserId).get();
        User currentUser = userRepository.findById(currentUserId).get();
        if (Objects.equals(currentUserId, targetUserId) || targetUser.getFollower().contains(currentUserId) || currentUser.getFollower().contains(targetUserId)) {
            return postRepository.findPostByPostedBy(targetUser);
        }
        return List.of();
    }

    public Post getPost(String currentUserId, String targetUserId, String postId) {
        if (!userRepository.existsById(targetUserId)) {
            throw new IllegalArgumentException("This account isn't exited");
        }
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("This post isn't exited");
        }
        User targetUser = userRepository.findById(targetUserId).get();
        if (Objects.equals(currentUserId, targetUserId) || targetUser.getFollower().contains(currentUserId) || targetUser.getFollowing().contains(currentUserId)) {
            return postRepository.findPostById(postId);
        }
        return null;
    }

    public String deletePost(String currentUserId, String targetUserId, String postId) {
        if (!userRepository.existsById(targetUserId)) {
            throw new IllegalArgumentException("This account isn't exited");
        }
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("This post isn't exited");
        }

        Post post = postRepository.findPostById(postId);
        if (Objects.equals(currentUserId, targetUserId)) {
            postRepository.delete(post);
            return "Successfully deleted post";
        }
        return "You don't have permission to delete this post";
    }

    public String likeOrDislikePost(String currentUserId, String targetUserId, String postId) {
        if (!userRepository.existsById(targetUserId)) {
            throw new IllegalArgumentException("This account isn't exited");
        }
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("This post isn't exited");
        }
        User targetUser = userRepository.findById(targetUserId).get();
        User currentUser = userRepository.findById(currentUserId).get();
        Post post = postRepository.findPostById(postId);
        if (Objects.equals(currentUserId, targetUserId) || targetUser.getFollower().contains(currentUserId) || targetUser.getFollowing().contains(currentUserId)) {
            if (post.getLike().contains(currentUser)) {
                post.getLike().remove(currentUser);
                postRepository.save(post);
                return "Successfully disliked post";
            } else {
                post.getLike().add(currentUser);
                postRepository.save(post);
                return "Successfully liked post";
            }
        }
        return "Something went wrong";
    }

    public Post addReplyToPost(String currentUserId, String targetUserId, String postId, Reply reply) {
        if (!userRepository.existsById(targetUserId)) {
            throw new IllegalArgumentException("This account isn't exited");
        }
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("This post isn't exited");
        }
        User targetUser = userRepository.findById(targetUserId).get();
        User currentUser = userRepository.findById(currentUserId).get();
        Post post = postRepository.findPostById(postId);
        if (Objects.equals(currentUserId, targetUserId) || targetUser.getFollower().contains(currentUserId) || targetUser.getFollowing().contains(currentUserId)) {
            reply.setUserId(currentUser);
            reply.setUsername(currentUser.getUsername());
            reply.setUserProfilePic(currentUser.getProfilePic());
            reply.setCreatedAt(new Date());
            post.getReplies().add(reply);
            postRepository.save(post);
            return post;
        }
        return null;
    }

    public FeedResponse getFeed(String currentUserId, int page, int pageSize) {
        User currentUser = userRepository.findById(currentUserId).get();
        List<String> followings = currentUser.getFollowing();
        List<String> followers = currentUser.getFollower();
        List<User> followingsUser = new ArrayList<>();

        for (String following : followings) {
            User user = userRepository.findById(following).get();
            followingsUser.add(user);
        }
        for (String follower : followers) {
            User user = userRepository.findById(follower).get();
            followingsUser.add(user);
        }
//        List<Post> feedPosts = postRepository.findPostsByPostedByIn(followingsUser);
        List<Post> feedPosts;
        Pageable pageable = PageRequest.of(page, pageSize);
        feedPosts = postRepository.findPostsByPostedByInAndBelongsToIsNullOrderByCreatedAtDesc(followingsUser, pageable);
        long totalSize = postRepository.countByPostedByInAndBelongsToIsNull(followingsUser);
        return new FeedResponse(feedPosts, totalSize);
    }

    public List<Post> getForCommunity(String currentUserId, int page, int pageSize) {
        User currentUser = userRepository.findById(currentUserId).get();
        Pageable pageable = PageRequest.of(page, pageSize);
        return postRepository.findPostsByPostedByIsNotOrderByCreatedAtDesc(currentUser, pageable);
    }

    public List<User> getTopCreator(String currentUserId) {
        if (userRepository.findById(currentUserId).isEmpty()) {
            throw new IllegalArgumentException("You are not logged in");
        }
        List<Post> allPosts = postRepository.findAll();
        HashMap<User, Integer> postCounts = new HashMap<>();
        HashMap<User, Integer> totalLikes = new HashMap<>();

        for (Post post : allPosts) {
            User user = post.getPostedBy();
            postCounts.put(user, postCounts.getOrDefault(user, 0) + 1);
            totalLikes.put(user, totalLikes.getOrDefault(user, 0) + post.getLike().size());
        }

        return postCounts.keySet().stream().sorted((p1, p2)->{
            int postCompare = Integer.compare(postCounts.get(p2), postCounts.get(p1));
            if (postCompare!=0) return postCompare;
            return Integer.compare(totalLikes.get(p1), totalLikes.get(p2));
        }).collect(Collectors.toList());
    }

    public FeedResponse getPopularPost(int page, int size) {
        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_WEEK, -365);
        Date startDate = calendar.getTime();

        int skip = (page-1)*size;

        return new FeedResponse(postRepository.findTopPost(startDate, endDate, skip, size), postRepository.countTopPosts(startDate, endDate));
    }

    public FeedResponse getAll(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new FeedResponse(postRepository.findPostsByCaptionContainsIgnoreCaseOrTagsContainsIgnoreCaseOrderByCreatedAtDesc(query, query, pageable), postRepository.countPostsByCaptionContainsIgnoreCaseOrTagsContainsIgnoreCase(query, query));
    }

    
}
