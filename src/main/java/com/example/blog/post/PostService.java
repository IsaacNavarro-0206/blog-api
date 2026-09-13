package com.example.blog.post;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public PostService(PostRepository postRepository,
            CategoryRepository categoryRepository,
            TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
    }

    public BlogPostResponse create(CreatePostRequest request) {
        Category category = categoryRepository.findByName(request.getCategory()).orElseGet(() -> {
            Category newCategory = new Category();

            newCategory.setName(request.getCategory());
            newCategory.setCreatedAt(Instant.now());
            newCategory.setUpdatedAt(Instant.now());

            return categoryRepository.save(newCategory);
        });

        Set<Tag> tags = request.getTags().stream().map(tagName -> tagRepository.findByName(tagName).orElseGet(() -> {
            Tag newTag = new Tag();

            newTag.setName(tagName);
            newTag.setCreatedAt(Instant.now());
            newTag.setUpdatedAt(Instant.now());

            return tagRepository.save(newTag);
        })).collect(Collectors.toSet());

        Post post = new Post();

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(category);
        post.setTags(tags);

        Instant now = Instant.now();
        post.setCreatedAt(now);
        post.setUpdatedAt(now);

        Post saved = postRepository.save(post);

        return toResponse(saved);
    }

    public List<BlogPostResponse> getAll(String term) {
        List<Post> posts = postRepository.findAll();

        if (term != null && !term.isBlank()) {
            String lowerTerm = term.toLowerCase();

            posts = posts.stream().filter(post -> post.getTitle().toLowerCase().contains(lowerTerm)
                    || post.getContent().toLowerCase().contains(lowerTerm)
                    || post.getCategory().getName().toLowerCase().contains(lowerTerm)).collect(Collectors.toList());
        }

        return posts.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public BlogPostResponse getById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id " + id));

        return toResponse(post);
    }

    public BlogPostResponse update(Long id, UpdatePostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id " + id));

        Category category = getOrCreateCategory(request.getCategory());

        Set<Tag> tags = request.getTags().stream().map(this::getOrCreateTag).collect(Collectors.toSet());

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(category);
        post.setTags(tags);
        post.setUpdatedAt(Instant.now());

        Post saved = postRepository.save(post);

        return toResponse(saved);
    }

    public void delete(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post not found with id " + id);
        }

        postRepository.deleteById(id);
    }

    private BlogPostResponse toResponse(Post post) {
        return new BlogPostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCategory() != null ? post.getCategory().getName() : null,
                post.getTags() != null
                        ? post.getTags().stream()
                                .map(Tag::getName)
                                .collect(Collectors.toList())
                        : List.of(),
                post.getCreatedAt(),
                post.getUpdatedAt());
    }

    private Category getOrCreateCategory(String categoryName) {
        return categoryRepository.findByName(categoryName)
                .orElseGet(() -> {
                    Category category = new Category();

                    category.setName(categoryName);
                    category.setCreatedAt(Instant.now());
                    category.setUpdatedAt(Instant.now());

                    return categoryRepository.save(category);
                });
    }

    private Tag getOrCreateTag(String tagName) {
        return tagRepository.findByName(tagName)
                .orElseGet(() -> {
                    Tag tag = new Tag();

                    tag.setName(tagName);
                    tag.setCreatedAt(Instant.now());
                    tag.setUpdatedAt(Instant.now());

                    return tagRepository.save(tag);
                });
    }
}
