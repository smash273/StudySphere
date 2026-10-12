package com.studysphere.servlet;

import com.studysphere.model.Document;
import com.studysphere.model.User;
import com.studysphere.service.DocumentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

@WebServlet("/upload")
@MultipartConfig(maxFileSize = 52428800, maxRequestSize = 52428800)
public class UploadServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UploadServlet.class);
    private final DocumentService documentService = new DocumentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        req.getRequestDispatcher("/jsp/upload.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        try {
            Part filePart = req.getPart("file");
            String title = req.getParameter("title");
            String fileName = getFileName(filePart);

            if (title == null || title.isBlank()) {
                title = fileName != null ? fileName.replaceFirst("[.][^.]+$", "") : "Untitled";
            }

            Document doc = documentService.uploadAndProcessAsync(
                    user.getId(), title, fileName, filePart.getInputStream(), filePart.getSize(), null
            ).join();

            req.setAttribute("successMessage", "Document \"" + doc.getTitle() + "\" processed — " + doc.getTotalTopics() + " topics extracted!");
            req.getRequestDispatcher("/jsp/upload.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.error("Upload failed: {}", e.getMessage(), e);
            req.setAttribute("errorMessage", "Upload failed: " + e.getMessage());
            req.getRequestDispatcher("/jsp/upload.jsp").forward(req, resp);
        }
    }

    private String getFileName(Part part) {
        String header = part.getHeader("content-disposition");
        if (header != null) {
            for (String token : header.split(";")) {
                if (token.trim().startsWith("filename")) {
                    return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
                }
            }
        }
        return "unknown";
    }
}
