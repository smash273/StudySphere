package com.studysphere.servlet;

import com.studysphere.model.RevisionRecord;
import com.studysphere.model.User;
import com.studysphere.service.RevisionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;

@WebServlet("/revision")
public class RevisionServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RevisionServlet.class);
    private final RevisionService revisionService = new RevisionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        try {
            List<RevisionRecord> records = revisionService.getRevisionRecordsForUser(user.getId());
            req.setAttribute("records", records);
            req.getRequestDispatcher("/jsp/revision.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.error("Failed to load revision data: {}", e.getMessage(), e);
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        try {
            int topicId = Integer.parseInt(req.getParameter("topicId"));
            revisionService.markAsRevised(user.getId(), topicId, "Revised via web interface");
            resp.sendRedirect(req.getContextPath() + "/revision");
        } catch (Exception e) {
            logger.error("Failed to mark revised: {}", e.getMessage(), e);
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
        }
    }
}
