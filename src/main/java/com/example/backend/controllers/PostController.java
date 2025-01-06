package com.example.backend.controllers;

import com.example.backend.models.Post;
import com.example.backend.models.Reply;
import com.example.backend.models.User;
import com.example.backend.repository.PostRepository;
import com.example.backend.response.TopCreator;
import com.example.backend.services.PostService;
import com.example.backend.utils.AuthenticationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api/v1/post")
@RestController
@CrossOrigin
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private AuthenticationUtil authenticationUtil;

    @PostMapping("/upload-post")
    public ResponseEntity<Post> uploadPost(@RequestBody @Valid Optional<Post> postRequest, HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            Post post = postService.createPost(postRequest, isAuthenticated);
            return ResponseEntity.ok().body(post);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/see-all-post/{targetUserId}")
    public ResponseEntity<List<Post>> getAllPost(@PathVariable String targetUserId, HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            List<Post> posts = postService.getAllPosts(isAuthenticated, targetUserId);
            return ResponseEntity.ok().body(posts);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/{targetUserId}/{postId}")
    public ResponseEntity<Post> getPost(@PathVariable String targetUserId, @PathVariable String postId, HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            Post post = postService.getPost(isAuthenticated, targetUserId, postId);
            return ResponseEntity.ok().body(post);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @DeleteMapping("/delete-post/{postId}")
    public ResponseEntity<String> deletePost(@RequestParam String targetUserId, @PathVariable String postId, HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            return ResponseEntity.ok().body(postService.deletePost(isAuthenticated, targetUserId, postId));
        }
        return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/interact-post/{postId}")
    public ResponseEntity<String> interactPost(@PathVariable String postId, @RequestParam String targetUserId, HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            return ResponseEntity.ok().body(postService.likeOrDislikePost(isAuthenticated, targetUserId, postId));
        }
        return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/reply-post/{postId}")
    public ResponseEntity<Post> replyPost(@PathVariable String postId, @RequestParam String targetUserId,@RequestBody Reply replyRequest, HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            return ResponseEntity.ok().body(postService.addReplyToPost(isAuthenticated, targetUserId, postId, replyRequest));
        }
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/feed")
    public ResponseEntity getFeedPosts(HttpServletRequest request, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "0") int pageSize) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            return ResponseEntity.ok().body(postService.getFeed(isAuthenticated, page, pageSize));
        }
        return ResponseEntity.badRequest().body("Please login first");
    }

    @GetMapping("/feed-for-community")
    public ResponseEntity<List<Post>> getFeedForCommunity(HttpServletRequest request, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "0") int pageSize) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            return ResponseEntity.ok().body(postService.getForCommunity(isAuthenticated, page, pageSize));
        }
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/top-creator")
    public ResponseEntity<List<User>> getTopUser(HttpServletRequest request) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            return ResponseEntity.ok().body(postService.getTopCreator(isAuthenticated));
        }
        return ResponseEntity.badRequest().body(null);
    }

    @PutMapping("/edit-post/{postId}")
    public ResponseEntity editPost(@RequestBody @Valid Post post, HttpServletRequest request, @PathVariable String postId) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            return ResponseEntity.ok().body(postService.editPost(isAuthenticated, postId, post));
        }
        return ResponseEntity.badRequest().body("Something went wrong");
    }

    @PostMapping("/save/{postId}")
    public ResponseEntity savePost(HttpServletRequest request, @PathVariable String postId) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            return ResponseEntity.ok().body(postService.savedPost(isAuthenticated, postId));
        }
        return ResponseEntity.badRequest().body("Cannot save this post");
    }

    @GetMapping("/save")
    public ResponseEntity seeSavePost(HttpServletRequest request, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated!=null) {
            return ResponseEntity.ok().body(postService.getSavedPosts(isAuthenticated, page, pageSize));
        }
        return ResponseEntity.badRequest().body("Something went wrong");
    }

    @GetMapping("/popular-post")
    public ResponseEntity getPopularPost(@RequestParam int page, @RequestParam int size, @RequestParam String userId) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPopularPost(page, size, userId));
    }

    @GetMapping("/get-all")
    public ResponseEntity getAll(@RequestParam String query, @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getAll(query, page, size));
    }
}
