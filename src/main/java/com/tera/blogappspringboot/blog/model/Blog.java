package com.tera.blogappspringboot.blog.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "blogs")
public class Blog {
    @Id
    private String id;

    private String title;
    private String slug;
    private String content;
    private String author;
    private String image;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
