package com.tera.blogappspringboot.blog.dto;

import com.tera.blogappspringboot.blog.validator.ValidImage;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogCreateRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @ValidImage(message = "Image is required and must be <= 2MB")
    private MultipartFile image;
}
