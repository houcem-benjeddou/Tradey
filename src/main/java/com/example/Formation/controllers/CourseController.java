package com.example.Formation.controllers;


import com.example.Formation.entities.Course;
import com.example.Formation.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin("*")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping("/formation/{formationId}")
    public List<Course> getCoursesByFormationId(@PathVariable Long formationId) {
        return courseService.getCoursesByFormationId(formationId);
    }

    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        return courseService.saveCourse(course);
    }

    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }
}
