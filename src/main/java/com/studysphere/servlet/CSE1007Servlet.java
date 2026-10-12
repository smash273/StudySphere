package com.studysphere.servlet;

import com.studysphere.config.DatabaseConnectionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/cse1007")
public class CSE1007Servlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean dbOk = DatabaseConnectionManager.getInstance().testConnection();
        req.setAttribute("dbStatus", dbOk ? "Connected (PostgreSQL + HikariCP)" : "Connection Error");
        req.setAttribute("dbOk", dbOk);
        req.getRequestDispatcher("/jsp/cse1007.jsp").forward(req, resp);
    }
}
