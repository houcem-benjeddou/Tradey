package com.example.Formation.services;

import com.example.Formation.entities.Answer;
import com.example.Formation.entities.Quiz;
import com.example.Formation.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizService {
    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private EmailService emailService;

    public List<Quiz> getQuizzesByCourseId(Long courseId) {
        return quizRepository.findByCourseId(courseId);
    }

    public Quiz getQuizById(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    public Quiz saveQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public void deleteQuiz(Long quizId) {
        quizRepository.deleteById(quizId);
    }

    public int calculateScore(Long quizId, List<Long> answerIds, String userEmail) {
        Quiz quiz = getQuizById(quizId);
        int correctAnswers = 0;

        for (Long answerId : answerIds) {
            // Check if the answer ID is correct
            Answer answer = quiz.getAnswers().stream()
                    .filter(a -> a.getId().equals(answerId))
                    .findFirst()
                    .orElse(null);

            if (answer != null && answer.isCorrect()) {
                correctAnswers++;
            }
        }

        int scorePercentage = (correctAnswers * 100) / answerIds.size();

        // Send email with score
        String subject = "Votre score au quiz";
        String body = "Bonjour,\n\nVous avez terminé le quiz avec un score de : "
                + scorePercentage + "%.\n\nMerci d'avoir participé !";

        emailService.sendEmail(userEmail, subject, body);

        return scorePercentage;
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }
}