package com.studysphere.servlet;

import com.studysphere.model.Topic;
import com.studysphere.model.User;
import com.studysphere.service.TopicService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

@WebServlet("/summary")
public class SummaryServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SummaryServlet.class);
    private final TopicService topicService = new TopicService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        try {
            String topicIdStr = req.getParameter("topicId");
            if (topicIdStr != null) {
                int topicId = Integer.parseInt(topicIdStr);
                Topic topic = topicService.getTopicDetails(topicId);
                req.setAttribute("topic", topic);
                req.setAttribute("summary", topic.getSummary());
                req.setAttribute("importantPoints", topic.getImportantPoints());
                req.setAttribute("definitions", topic.getDefinitions());
                req.setAttribute("formulas", topic.getFormulas());
                req.setAttribute("keywords", topic.getKeywords());
            }
            req.getRequestDispatcher("/jsp/notes.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.error("Failed to load summary: {}", e.getMessage(), e);
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }
}
