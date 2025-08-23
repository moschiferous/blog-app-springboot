package com.tera.blogappspringboot.blog.controller;

import com.tera.blogappspringboot.blog.dto.BlogCreateRequest;
import com.tera.blogappspringboot.blog.dto.BlogResponse;
import com.tera.blogappspringboot.blog.dto.BlogUpdateRequest;
import com.tera.blogappspringboot.blog.model.Blog;
import com.tera.blogappspringboot.blog.service.BlogService;
import com.tera.blogappspringboot.payload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @GetMapping
    public ResponseEntity<List<BlogResponse>> getAll() {
        return ResponseEntity.ok(blogService.getAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @ModelAttribute BlogCreateRequest blogCreateRequest, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> Objects.requireNonNullElse(error.getDefaultMessage(), "Validation error"))
                    .toList();
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Validation failed",
                    "errors", errors
            ));
        }

        try {
            BlogResponse savedBlog = blogService.create(blogCreateRequest, principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBlog);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<BlogResponse> getBySlug(@PathVariable String slug) {
        return blogService.getBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Blog> getById(@PathVariable String id) {
        return ResponseEntity.ok(blogService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable String id, @Valid @ModelAttribute BlogUpdateRequest blogUpdateRequest, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> Objects.requireNonNullElse(error.getDefaultMessage(), "Validation error"))
                    .toList();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(errors, "1"));
        }
        BlogResponse updated = blogService.update(id, blogUpdateRequest, principal.getName());
        return ResponseEntity.ok(ApiResponse.success(updated, "Success"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable String id, Principal principal) {
        blogService.delete(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.success(null, "Success"));
    }
}
