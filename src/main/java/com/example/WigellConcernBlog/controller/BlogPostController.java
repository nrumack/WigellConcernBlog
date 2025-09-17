package com.example.WigellConcernBlog.controller;

import com.example.WigellConcernBlog.DTO.BlogPostDTO;
import com.example.WigellConcernBlog.model.BlogPost;
import com.example.WigellConcernBlog.service.BlogPostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2")
public class BlogPostController {
    private final BlogPostService blogPostService;

    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    @GetMapping("/posts")
    public ResponseEntity<List<BlogPostDTO>> getAllBlogPosts() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Principal: " + auth.getName());
        auth.getAuthorities().forEach(a -> System.out.println("Authority: " + a.getAuthority()));
        return ResponseEntity.ok(blogPostService.getAllBlogPosts());
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<BlogPostDTO> getPostById(@PathVariable long id) {
        return ResponseEntity.ok(blogPostService.getPostById(id));
    }
    @PreAuthorize("hasRole('myclient_USER')")
    @PostMapping("/newpost")
    public ResponseEntity<?> createBlogPost(@RequestBody BlogPostDTO blogPostDTO, @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        String subject = jwt.getSubject();

        System.out.println("New post was made by " + subject);
        BlogPost savedBlogPost = blogPostService.createBlogPost(blogPostDTO, username);
        return ResponseEntity.ok("Blogpost was created: " + savedBlogPost.getBlogPostId());
    }

    @PreAuthorize("hasRole('myclient_USER') and @blogPostService.isOwner(#id, authentication.name)")
    @PutMapping("/updatepost/{id}")
    public ResponseEntity<?> updateBlogPost(@PathVariable Long id, @RequestBody BlogPostDTO blogPostDTO, @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        if (!blogPostService.isOwner(id, username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not allowed to update this blogpost");
        }
        return ResponseEntity.ok(blogPostService.updateBlogPost(id, blogPostDTO, username));
    }

    @PreAuthorize("hasRole('myclient_ADMIN') or @blogPostService.isOwner(#id, authentication.name)")
    @DeleteMapping("/deletepost/{id}")
    public ResponseEntity<String> deleteBlogPost(@PathVariable long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Deleted by " + auth.getName());
        blogPostService.deleteBlogPost(id);
        return ResponseEntity.ok("Blogpost was deleted: " + id);
    }

    @PreAuthorize("hasRole('myclient_ADMIN')")
    @GetMapping("/count")
    public ResponseEntity<Long> countBlogPosts() {
        return ResponseEntity.ok(blogPostService.blogPostCount());
    }
}
