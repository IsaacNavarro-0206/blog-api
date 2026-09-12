package com.example.blog.post;

import org.springframework.stereotype.Service;

import com.example.demo.post.BlogPostResponse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final Map<Long, BlogPost> posts = new HashMap<>();
    private Long nextId = 1L;

    public BlogPostResponse create(CreatePostRequest request) {
        BlogPost post = new BlogPost();

        post.setId(nextId++);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(request.getCategory());
        post.setTags(request.getTags());

        Instant now = Instant.now();
        post.setCreatedAt(now);
        post.setUpdatedAt(now);

        posts.put(post.getId(), post);

        return BlogPostResponse.from(post);
    }

    public List<BlogPostResponse> getAll(String term) {
        List<BlogPost> result = new ArrayList<>(posts.values());

        if (term != null && !term.isBlank()) {
            String lowerTerm = term.toLowerCase();

            result = result.stream().filter(post -> post.getTitle().toLowerCase().contains(lowerTerm)
                    || post.getContent().toLowerCase().contains(lowerTerm)
                    || post.getCategory().toLowerCase().contains(lowerTerm)).collect(Collectors.toList());
        }

        return result.stream().map(BlogPostResponse::from).collect(Collectors.toList());
    }

    public BlogPostResponse getById(Long id) {
        BlogPost post = posts.get(id);

        if (post == null) {
            throw new ResourceNotFoundException("Post not found with id " + id);
        }

        return BlogPostResponse.from(post);
    }

    public BlogPostResponse update(Long id, UpdatePostRequest request) {
        BlogPost post = posts.get(id);

        if (post == null) {
            throw new ResourceNotFoundException("Post not found with id " + id);
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(request.getCategory());
        post.setTags(request.getTags());
        post.setUpdatedAt(Instant.now());

        return BlogPostResponse.from(post);
    }

    public void delete(Long id) {
        BlogPost removed = posts.remove(id);

        if (removed == null) {
            throw new ResourceNotFoundException("Post not found with id " + id);
        }
    }
}
