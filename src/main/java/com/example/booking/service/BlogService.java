package com.example.booking.service;

import com.example.booking.entity.Blog;
import com.example.booking.repository.BlogRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BlogService {
    private final BlogRepository blogRepository;
    public BlogService(BlogRepository blogRepository){
        this.blogRepository = blogRepository;
    }
    public List<Blog> showList(){
        return blogRepository.findAll();
    }
    public Blog createBlog(Blog blog){
        Blog newBlog = new Blog();
        newBlog.setBlogName(blog.getBlogName());
        newBlog.setBlogStatus(true);
        newBlog.setPostDate(new Date());
        newBlog.setContent(blog.getContent());
        blogRepository.save(blog);
        return blog;
    }
    public Blog checkBlog(int blogId){
        return blogRepository.findById(blogId)
                .orElseThrow(()-> new RuntimeException("Blog not found"));
    }
    public Blog updateBlog(Blog blog){
        Blog exstingBlog = checkBlog(blog.getBlogId());
        exstingBlog.setContent(blog.getContent());
        exstingBlog.setBlogStatus(blog.getBlogStatus());
        exstingBlog.setBlogName(blog.getBlogName());
        blogRepository.save(exstingBlog);
        return blog;
    }

}
