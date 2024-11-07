package com.example.vim.repository;

import com.example.vim.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    @Query("SELECT v FROM Video v JOIN v.assignedUsers u WHERE u.id = :userId")
    List<Video> findVideosByAssignedUserId(@Param("userId") Long userId);
}
