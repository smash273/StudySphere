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
import java.util.List;

@WebServlet("/topics")
public class TopicServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(TopicServlet.class);
    private final TopicService topicService = new TopicService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        try {
            String docIdStr = req.getParameter("documentId");
            if (docIdStr != null) {
                int docId = Integer.parseInt(docIdStr);
                List<Topic> topics = topicService.getTopicsByDocumentId(docId);
                req.setAttribute("topics", topics);
                req.setAttribute("documentId", docId);
            }
            req.getRequestDispatcher("/jsp/topic.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.error("Failed to load topics: {}", e.getMessage(), e);
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }
}
