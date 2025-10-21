package com.example.WigellConcernBlog.mappers;

import com.example.WigellConcernBlog.DTO.BlogPostDTO;
import com.example.WigellConcernBlog.model.BlogPost;
import org.springframework.stereotype.Component;

@Component
public class BlogPostMapper {
    public BlogPostDTO toDTO(BlogPost blogPost) {
        return new BlogPostDTO(
                blogPost.getBlogPostId(),
                blogPost.getTitle(),
                blogPost.getContent(),
                blogPost.getKeycloakId());
    }
}
