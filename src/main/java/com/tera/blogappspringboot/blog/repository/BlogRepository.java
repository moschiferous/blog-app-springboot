package com.tera.blogappspringboot.blog.repository;

import com.tera.blogappspringboot.blog.dto.BlogResponse;
import com.tera.blogappspringboot.blog.model.Blog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BlogRepository extends MongoRepository<Blog, String> {
    Optional<BlogResponse> findBySlug(String slug);
}
