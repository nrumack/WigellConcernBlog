package com.example.WigellConcernBlog.service;

import com.example.WigellConcernBlog.DTO.BlogPostDTO;
import com.example.WigellConcernBlog.exceptions.BlogPostNotFoundException;
import com.example.WigellConcernBlog.exceptions.UnauthorizedAccessException;
import com.example.WigellConcernBlog.mappers.BlogPostMapper;
import com.example.WigellConcernBlog.model.BlogPost;
import com.example.WigellConcernBlog.repository.BlogPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogPostService {
    private final BlogPostRepository blogPostRepository;
    private final BlogPostMapper blogPostMapper;

    @Autowired
    public BlogPostService(BlogPostRepository blogPostRepository, BlogPostMapper blogPostMapper) {
        this.blogPostRepository = blogPostRepository;
        this.blogPostMapper = blogPostMapper;
    }

    public boolean isOwner(Long blogPostId, String keycloakId) {
        BlogPost blogPost = blogPostRepository.findById(blogPostId)
                .orElseThrow(() -> new BlogPostNotFoundException(blogPostId));
        return blogPost.getKeycloakId().equals(keycloakId);
    }

    public List<BlogPostDTO> getAllBlogPosts() {
        return blogPostRepository.findAll().stream()
                .map(blogPostMapper::toDTO)
                .toList();
    }

    public BlogPostDTO getPostById(Long blogPostId) {
        BlogPost post = blogPostRepository.findById(blogPostId)
                .orElseThrow(() -> new BlogPostNotFoundException(blogPostId));
        return blogPostMapper.toDTO(post);
    }

    public BlogPost createBlogPost(BlogPostDTO blogPostDTO, String keycloakId) {
        BlogPost blogPost = new BlogPost(
                blogPostDTO.title(),
                blogPostDTO.content(),
                keycloakId
        );
        return blogPostRepository.save(blogPost);
    }

    public BlogPostDTO updateBlogPost(Long blogPostId, BlogPostDTO blogPostDTO, String keycloakId) {
        BlogPost existingBlogPost = blogPostRepository.findById(blogPostId)
                .orElseThrow(() -> new BlogPostNotFoundException(blogPostId));

        if (!existingBlogPost.getKeycloakId().equals(keycloakId)) {
            throw new UnauthorizedAccessException("User is not owner of this post");
        }
        existingBlogPost.setTitle(blogPostDTO.title());
        existingBlogPost.setContent(blogPostDTO.content());

        return blogPostMapper.toDTO(blogPostRepository.save(existingBlogPost));
    }

    public void deleteBlogPost(Long blogPostId, String keycloakId) {
        BlogPost blogPost = blogPostRepository.findById(blogPostId)
                .orElseThrow(() -> new BlogPostNotFoundException(blogPostId));

        if (!blogPost.getKeycloakId().equals(keycloakId)) {
            throw new UnauthorizedAccessException("User is not owner of this post");
        }
        blogPostRepository.delete(blogPost);
    }

    public long blogPostCount() {
        return blogPostRepository.count();
    }
}
