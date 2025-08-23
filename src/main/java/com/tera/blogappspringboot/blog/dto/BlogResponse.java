package com.tera.blogappspringboot.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogResponse {
    private String id;
    private String title;
    private String slug;
    private String content;
    private String author;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
