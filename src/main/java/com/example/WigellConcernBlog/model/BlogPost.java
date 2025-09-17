package com.example.WigellConcernBlog.model;

import jakarta.persistence.*;

@Entity
@Table(name = "blogpost")
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long blogPostId;

    private String title;
    private String content;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    public BlogPost() {}

    public BlogPost(String title, String content, User author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public long getBlogPostId() {
        return blogPostId;
    }

    public void setBlogPostId(long blogPostId) {
        this.blogPostId = blogPostId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
