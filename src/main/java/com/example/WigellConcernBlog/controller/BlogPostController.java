package com.example.WigellConcernBlog.controller;

import com.example.WigellConcernBlog.DTO.BlogPostDTO;
import com.example.WigellConcernBlog.mappers.BlogPostMapper;
import com.example.WigellConcernBlog.model.BlogPost;
import com.example.WigellConcernBlog.service.BlogPostService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2")
public class BlogPostController {
    private final BlogPostService blogPostService;
    private final BlogPostMapper blogPostMapper;
    private static final Logger log = LoggerFactory.getLogger(BlogPostController.class);

    public BlogPostController(BlogPostService blogPostService, BlogPostMapper blogPostMapper) {
        this.blogPostService = blogPostService;
        this.blogPostMapper = blogPostMapper;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/posts")
    public ResponseEntity<List<BlogPostDTO>> getAllBlogPosts() {
        return ResponseEntity.ok(blogPostService.getAllBlogPosts());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/post/{id}")
    public ResponseEntity<BlogPostDTO> getPostById(@PathVariable long id) {
        return ResponseEntity.ok(blogPostService.getPostById(id));
    }

    @PreAuthorize("hasRole('myclient_USER')")
    @PostMapping("/newpost")
    public ResponseEntity<BlogPostDTO> createBlogPost(@Valid @RequestBody BlogPostDTO blogPostDTO, @AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getClaim("sub");
        log.info("User {} created a new blog post with title '{}' and body '{}'", keycloakId, blogPostDTO.title(), blogPostDTO.content());
        BlogPost savedBlogPost = blogPostService.createBlogPost(blogPostDTO, keycloakId);
        BlogPostDTO responseDTO = blogPostMapper.toDTO(savedBlogPost);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PreAuthorize("hasRole('myclient_USER') and @blogPostService.isOwner(#id, @security.getKeycloakId(authentication))")
    @PutMapping("/updatepost/{id}")
    public ResponseEntity<?> updateBlogPost(@Valid @PathVariable Long id, @RequestBody BlogPostDTO blogPostDTO, @AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getClaim("sub");
        log.info("User {} updated blog post {} with new title '{}' and/or body '{}'", keycloakId, id, blogPostDTO.title(), blogPostDTO.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(blogPostService.updateBlogPost(id, blogPostDTO, keycloakId));
    }

    @PreAuthorize("hasRole('myclient_ADMIN') or @blogPostService.isOwner(#id, @security.getKeycloakId(authentication))")
    @DeleteMapping("/deletepost/{id}")
    public ResponseEntity<String> deleteBlogPost(@PathVariable long id, @AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getClaim("sub");
        log.info("Blog post {} was deleted by user {}", id, keycloakId);
        blogPostService.deleteBlogPost(id, keycloakId);
        return ResponseEntity.ok("Blogpost was deleted: " + id);
    }

    @PreAuthorize("hasRole('myclient_ADMIN')")
    @GetMapping("/count")
    public ResponseEntity<Long> countBlogPosts() {
        return ResponseEntity.ok(blogPostService.blogPostCount());
    }
}
