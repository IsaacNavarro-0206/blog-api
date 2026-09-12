package com.example.blog.post;

import java.time.Instant;
import java.util.List;

public class BlogPostResponse {
    private Long id;
    private String title;
    private String content;
    private String category;
    private List<String> tags;
    private Instant createdAt;
    private Instant updatedAt;

    public static BlogPostResponse from(BlogPost post) {
        return new BlogPostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCategory(),
                post.getTags(),
                post.getCreatedAt(),
                post.getUpdatedAt());
    }

    public BlogPostResponse(Long id, String title, String content,
            String category, List<String> tags,
            Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.tags = tags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getTags() {
        return tags;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
