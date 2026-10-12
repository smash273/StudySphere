package com.studysphere.servlet;

import com.studysphere.model.User;
import com.studysphere.service.AskNotesService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

@WebServlet("/ask-notes")
public class AskNotesServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AskNotesServlet.class);
    private final AskNotesService askNotesService = new AskNotesService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        req.getRequestDispatcher("/jsp/ask-notes.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String question = req.getParameter("question");
        try {
            AskNotesService.GroundedAnswerResult result = askNotesService.askQuestion(user.getId(), question);
            req.setAttribute("question", question);
            req.setAttribute("answer", result.getAnswer());
            req.setAttribute("citations", result.getCitations());
        } catch (Exception e) {
            logger.error("Ask notes failed: {}", e.getMessage(), e);
            req.setAttribute("question", question);
            req.setAttribute("errorMessage", "Could not answer: " + e.getMessage());
        }
        req.getRequestDispatcher("/jsp/ask-notes.jsp").forward(req, resp);
    }
}
