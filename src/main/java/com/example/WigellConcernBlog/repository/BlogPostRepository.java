package com.example.WigellConcernBlog.repository;

import com.example.WigellConcernBlog.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findAll();
    Optional<BlogPost> findById(Long id);
}
