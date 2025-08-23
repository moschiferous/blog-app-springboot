package com.tera.blogappspringboot.blog.service;

import com.tera.blogappspringboot.blog.dto.BlogCreateRequest;
import com.tera.blogappspringboot.blog.dto.BlogResponse;
import com.tera.blogappspringboot.blog.dto.BlogUpdateRequest;
import com.tera.blogappspringboot.blog.model.Blog;

import java.util.List;
import java.util.Optional;

public interface BlogService {
    List<BlogResponse> getAll();
    BlogResponse create(BlogCreateRequest blogCreateRequest, String author);
    Optional<BlogResponse> getBySlug(String slug);
    Blog getById(String id);
    BlogResponse update(String id, BlogUpdateRequest blogUpdateRequest, String currentUser);
    void delete(String id, String currentUser);
}
