package com.example.WigellConcernBlog.service;

import com.example.WigellConcernBlog.DTO.BlogPostDTO;
import com.example.WigellConcernBlog.model.BlogPost;
import com.example.WigellConcernBlog.model.User;
import com.example.WigellConcernBlog.repository.BlogPostRepository;
import com.example.WigellConcernBlog.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogPostService {
    private final BlogPostRepository blogPostRepository;
    private final UserRepository userRepository;

    public boolean isOwner(Long postId, String username) {
        BlogPost blogPost = blogPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("BlogPost not found"));
        return blogPost.getAuthor().getUsername().equals(username);
    }

    @Autowired
    public BlogPostService(BlogPostRepository blogPostRepository, UserRepository userRepository) {
        this.blogPostRepository = blogPostRepository;
        this.userRepository = userRepository;
    }

    public List<BlogPostDTO> getAllBlogPosts() {
        List<BlogPost> posts = blogPostRepository.findAll();
        return posts.stream()
                .map(post -> new BlogPostDTO(
                        post.getBlogPostId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getAuthor().getUsername())
                        )
                .toList();
    }

    public BlogPostDTO getPostById(Long id) {
        BlogPost post = blogPostRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
        return new BlogPostDTO(post.getBlogPostId(), post.getTitle(), post.getContent(), post.getAuthor().getUsername());
    }

    public BlogPost createBlogPost(BlogPostDTO blogPostDTO, String username) {
        User author = userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User user = new User(username, "", "USER");
                    return userRepository.save(user);
                });

        BlogPost blogPost = new BlogPost(
                blogPostDTO.title(),
                blogPostDTO.content(),
                author
        );
        return blogPostRepository.save(blogPost);
    }

    public BlogPostDTO updateBlogPost(Long id, BlogPostDTO blogPostDTO, String username) {
        BlogPost existingBlogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BlogPost not found"));

        if (!existingBlogPost.getAuthor().getUsername().equals(username)) {
            throw new EntityNotFoundException("User is not owner of this post");
        }
        existingBlogPost.setTitle(blogPostDTO.title());
        existingBlogPost.setContent(blogPostDTO.content());

        BlogPost updatedBlogPost = blogPostRepository.save(existingBlogPost);

        return new BlogPostDTO(updatedBlogPost.getBlogPostId(), updatedBlogPost.getTitle(), updatedBlogPost.getContent(), updatedBlogPost.getAuthor().getUsername());
    }

    public void deleteBlogPost(Long id) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BlogPost not found"));
        blogPostRepository.delete(blogPost);
    }

    public long blogPostCount() {
        return blogPostRepository.count();
    }
}
