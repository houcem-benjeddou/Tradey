package com.example.Formation.controllers;

import com.example.Formation.entities.Video;
import com.example.Formation.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/videos")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @GetMapping
    public List<Video> getAllVideos() {
        return videoService.getAllVideos();
    }

    @GetMapping("/course/{courseId}")
    public List<Video> getVideosByCourseId(@PathVariable Long courseId) {
        return videoService.getVideosByCourseId(courseId);
    }

    @PostMapping(value = "add", consumes = {"application/json", "application/json;charset=UTF-8"})
    public Video createVideo(@RequestBody Video video) {
        return videoService.saveVideo(video);
    }

    @DeleteMapping("/{id}")
    public void deleteVideo(@PathVariable Long id) {
        videoService.deleteVideo(id);
    }
}
