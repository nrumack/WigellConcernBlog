package com.example.WigellConcernBlog.exceptions;

public class BlogPostNotFoundException extends RuntimeException {
    public BlogPostNotFoundException(Long id) {
        super("Could not find blog post with id " + id);
    }
}
