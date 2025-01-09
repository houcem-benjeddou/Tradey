package com.example.Formation.services;

import com.example.Formation.entities.Course;
import com.example.Formation.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }


    public List<Course> getCoursesByFormationId(Long formationId) {
        return courseRepository.findAll().stream()
                .filter(course -> course.getFormation() != null && course.getFormation().getId().equals(formationId))
                .toList();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }
}
