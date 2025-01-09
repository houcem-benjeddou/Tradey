package com.example.Formation.services;

import com.example.Formation.entities.Course;
import com.example.Formation.entities.Video;
import com.example.Formation.repository.CourseRepository;
import com.example.Formation.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private CourseRepository courseRepository;

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public List<Video> getVideosByCourseId(Long courseId) {
        return videoRepository.findAll().stream()
                .filter(video -> video.getCourse() != null && video.getCourse().getId().equals(courseId))
                .toList();
    }

    public Video saveVideo(Video video) {
        Long courseId = video.getCourse().getId();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Le cours avec l'ID " + courseId + " n'existe pas."));
        video.setCourse(course);
        return videoRepository.save(video);
    }


    public void deleteVideo(Long id) {
        videoRepository.deleteById(id);
    }

}
