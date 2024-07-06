package com.example.booking.controller;

import com.example.booking.entity.Blog;
import com.example.booking.service.BlogService;
import com.example.booking.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/blogs")
public class BlogController {
    private final BlogService blogService;
    private final UserService userService;
    public BlogController(BlogService blogService, UserService userService) {
        this.blogService = blogService;
        this.userService = userService;
    }
    @GetMapping("/list")
    public String getAllBlogs(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 12);
        Page<Blog> blogPage = blogService.showList(pageable);
        model.addAttribute("blogs", blogPage.getContent());
        model.addAttribute("totalPages", blogPage.getTotalPages());
        model.addAttribute("currentPage", page);
        return "Blogs/list";
    }

    @GetMapping("/listBlogOfAdmin")
    public String getAllBlogsOfAdmin(@RequestParam(defaultValue = "0") int page, Model model) {

        try {
            Pageable pageable = PageRequest.of(page, 6);
            Page<Blog> blogPage = blogService.showListAdmin(pageable);
            model.addAttribute("blogs", blogPage.getContent());
            model.addAttribute("totalPages", blogPage.getTotalPages());
            model.addAttribute("currentPage", page);
            return "ListOfAdmin/listBlog";
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "ListOfAdmin/listBlog";
    }

    @GetMapping("/add")
    public String showAddFrom(@NotNull Model model) {
        try {
            Blog blog = new Blog();
            model.addAttribute("blog", blog);
            return "Blogs/add";
        }catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "errorPage";
        }
    }
    @PostMapping("/save")
    public String saveAddFrom(@Valid @ModelAttribute("blog") Blog blog, @NotNull BindingResult bindingResult,
                              Model model){
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "Blogs/add";
        }
        blog.setBlogStatus(false);
        blogService.createBlog(blog);
        return "redirect:/blogs/listBlogOfAdmin";
    }
    @GetMapping("/edit/{blogId}")
    public String showEditFrom(@PathVariable int blogId, Model model){
        try {
            Blog existingBlog = blogService.checkBlog(blogId);
            model.addAttribute("blog", existingBlog);
            return "Blogs/edit";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", "Room not found");
            return "errorPage";
        }
    }
    @PostMapping("/saveEdit/{blogId}")
    public String saveEditForm(@PathVariable int blogId, @Valid Blog blog, BindingResult result, Model model) {
        if (result.hasErrors()) {
            var errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            blog.setBlogId(blogId);
            return "Blogs/edit";
        }
        blogService.updateBlog(blog);
        return "redirect:/blogs/listBlogOfAdmin";
    }

}
