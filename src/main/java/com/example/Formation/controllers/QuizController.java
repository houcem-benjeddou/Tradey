package com.example.Formation.controllers;

import com.example.Formation.entities.Quiz;
import com.example.Formation.services.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin("*")
public class QuizController {
    @Autowired
    private QuizService quizService;

    @GetMapping("/course/{courseId}")
    public List<Quiz> getQuizzesByCourseId(@PathVariable Long courseId) {
        return quizService.getQuizzesByCourseId(courseId);
    }

    @PostMapping
    public Quiz createQuiz(@RequestBody Quiz quiz) {
        return quizService.saveQuiz(quiz);
    }

    @DeleteMapping("/{quizId}")
    public void deleteQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
    }

    @PostMapping("/{quizId}/score")
    public ResponseEntity<String> calculateScore(
            @PathVariable Long quizId,
            @RequestParam String userEmail,
            @RequestBody List<Long> answerIds) {

        int score = quizService.calculateScore(quizId, answerIds, userEmail);

        return ResponseEntity.ok("Quiz terminé ! Votre score est envoyé à l'adresse email : " + userEmail);
    }
}