package com.example.booking.service;

import com.example.booking.entity.Blog;
import com.example.booking.repository.BlogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class BlogService {
    private final BlogRepository blogRepository;
    public BlogService(BlogRepository blogRepository){
        this.blogRepository = blogRepository;
    }

    public Page<Blog> showList(Pageable pageable){
        return blogRepository.findByBlogStatusTrue(pageable);
    }
    public void createBlog(Blog blog){
        blogRepository.save(blog);
    }
    public Blog checkBlog(int blogId){
        return blogRepository.findById(blogId)
                .orElseThrow(()-> new RuntimeException("Blog not found"));
    }
    public void updateBlog(Blog blog){
        blogRepository.save(blog);
    }
    public Page<Blog> showListAdmin(Pageable pageable){
        return blogRepository.findAll(pageable);
    }

}
