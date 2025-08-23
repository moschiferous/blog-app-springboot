package com.tera.blogappspringboot.blog.service;

import com.tera.blogappspringboot.blog.dto.BlogCreateRequest;
import com.tera.blogappspringboot.blog.dto.BlogResponse;
import com.tera.blogappspringboot.blog.dto.BlogUpdateRequest;
import com.tera.blogappspringboot.blog.model.Blog;
import com.tera.blogappspringboot.blog.repository.BlogRepository;
import com.tera.blogappspringboot.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {
    private final BlogRepository blogRepository;

    @Override
    public List<BlogResponse> getAll(){
        return blogRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BlogResponse create(BlogCreateRequest blogCreateRequest, String author) {
        String baseSlug = SlugUtil.toSlug(blogCreateRequest.getTitle());
        String slug = baseSlug + "-" + SlugUtil.randomSuffix(5);

        MultipartFile image = blogCreateRequest.getImage();
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        Path path = Paths.get("uploads", fileName);

        try {
            Files.createDirectories(path.getParent());
            image.transferTo(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }

        Blog blog = new Blog();
        blog.setTitle(blogCreateRequest.getTitle());
        blog.setSlug(slug);
        blog.setContent(blogCreateRequest.getContent());
        blog.setAuthor(author);
        blog.setImage(fileName);
        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());
        Blog saved = blogRepository.save(blog);

        return mapToResponse(saved);
    }

    private BlogResponse mapToResponse(Blog blog) {
        return new BlogResponse(
                blog.getId(),
                blog.getTitle(),
                blog.getSlug(),
                blog.getContent(),
                blog.getAuthor(),
                blog.getImage(),
                blog.getCreatedAt(),
                blog.getUpdatedAt()
        );
    }

    @Override
    public Optional<BlogResponse> getBySlug(String slug) {
        return blogRepository.findBySlug(slug);
    }

    @Override
    public Blog getById(String id) {
        return blogRepository.findById(id).orElse(null);
    }

    @Override
    public BlogResponse update(String id, BlogUpdateRequest blogUpdateRequest, String currentUser) {
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new RuntimeException("Blog not found"));
        if (!blog.getAuthor().equals(currentUser)) {
            throw new RuntimeException("You are not allowed to update this blog");
        }

        String baseSlug = SlugUtil.toSlug(blogUpdateRequest.getTitle());
        String slug = baseSlug + "-" + SlugUtil.randomSuffix(5);

        blog.setTitle(blogUpdateRequest.getTitle());
        blog.setSlug(slug);
        blog.setContent(blogUpdateRequest.getContent());
        blog.setUpdatedAt(LocalDateTime.now());

        MultipartFile newImage = blogUpdateRequest.getImage();
        if (newImage != null && !newImage.isEmpty()) {
            try{
                String fileName = UUID.randomUUID() + "_" + newImage.getOriginalFilename();
                Path newImagePath = Paths.get("uploads", fileName);
                Files.createDirectories(newImagePath.getParent());
                newImage.transferTo(newImagePath);
                blog.setImage(fileName);

                Path oldImagePath = Paths.get("uploads", blog.getImage());
                Files.deleteIfExists(oldImagePath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save image", e);
            }
        }

        Blog updated = blogRepository.save(blog);
        return mapToResponse(updated);
    }

    @Override
    public void delete(String id, String currentUser){
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new RuntimeException("Blog not found"));
        if (!blog.getAuthor().equals(currentUser)) {
            throw new RuntimeException("You are not allowed to delete this blog");
        }

        try {
            Path imagePath = Paths.get("uploads", blog.getImage());
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        blogRepository.deleteById(id);
    }
}
