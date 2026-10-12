package com.studysphere.servlet;

import com.studysphere.model.Topic;
import com.studysphere.model.User;
import com.studysphere.service.SearchService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SearchServlet.class);
    private final SearchService searchService = new SearchService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String query = req.getParameter("q");
        if (query != null && !query.isBlank()) {
            try {
                List<Topic> results = searchService.search(user.getId(), query);
                req.setAttribute("results", results);
                req.setAttribute("query", query);
            } catch (Exception e) {
                logger.error("Search failed: {}", e.getMessage(), e);
                req.setAttribute("errorMessage", e.getMessage());
            }
        }
        req.getRequestDispatcher("/jsp/search.jsp").forward(req, resp);
    }
}
