package com.example.booking.repository;

import com.example.booking.entity.Blog;
import com.example.booking.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog,Integer> {
    Page<Blog> findByBlogStatusTrue(Pageable pageable);
}
