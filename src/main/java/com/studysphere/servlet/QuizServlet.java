package com.studysphere.servlet;

import com.studysphere.model.*;
import com.studysphere.service.QuizService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/quiz")
public class QuizServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(QuizServlet.class);
    private final QuizService quizService = new QuizService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String topicIdStr = req.getParameter("topicId");
        if (topicIdStr != null) {
            try {
                int topicId = Integer.parseInt(topicIdStr);
                Quiz quiz = quizService.getOrGenerateQuiz(topicId, 5);
                req.setAttribute("quiz", quiz);
            } catch (Exception e) {
                logger.error("Failed to load quiz: {}", e.getMessage(), e);
                req.setAttribute("errorMessage", e.getMessage());
            }
        }
        req.getRequestDispatcher("/jsp/quiz.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        try {
            int topicId = Integer.parseInt(req.getParameter("topicId"));
            String[] questionIds = req.getParameterValues("questionId");
            List<Answer> answers = new ArrayList<>();
            if (questionIds != null) {
                for (String qIdStr : questionIds) {
                    int qId = Integer.parseInt(qIdStr);
                    String selected = req.getParameter("answer_" + qId);
                    answers.add(new Answer(qId, selected != null ? selected : ""));
                }
            }
            QuizAttempt attempt = quizService.submitQuiz(user.getId(), topicId, answers);
            Quiz quiz = quizService.getOrGenerateQuiz(topicId, 5);
            req.setAttribute("quiz", quiz);
            req.setAttribute("attempt", attempt);
        } catch (Exception e) {
            logger.error("Quiz submission failed: {}", e.getMessage(), e);
            req.setAttribute("errorMessage", e.getMessage());
        }
        req.getRequestDispatcher("/jsp/quiz.jsp").forward(req, resp);
    }
}
