package com.studysphere.servlet;

import com.studysphere.model.Document;
import com.studysphere.model.User;
import com.studysphere.service.DocumentService;
import com.studysphere.service.RevisionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/documents")
public class DocumentServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(DocumentServlet.class);
    private final DocumentService documentService = new DocumentService();
    private final RevisionService revisionService = new RevisionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        try {
            List<Document> documents = documentService.getDocumentsForUser(user.getId());
            Map<String, Object> stats = revisionService.getDashboardStats(user.getId());
            req.setAttribute("documents", documents);
            req.setAttribute("totalDocuments", stats.get("totalDocuments"));
            req.setAttribute("totalTopics", stats.get("totalTopics"));
            req.setAttribute("revisedTopics", stats.get("revisedTopics"));
            req.setAttribute("completionPercent", stats.get("completionPercent"));
            req.getRequestDispatcher("/jsp/dashboard.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.error("Failed to load documents: {}", e.getMessage(), e);
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }
}
