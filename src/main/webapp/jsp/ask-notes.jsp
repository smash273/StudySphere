<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html><head>
    <title>StudySphere — Ask My Notes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head><body>
<div class="navbar">
    <span class="brand">StudySphere</span>
    <a href="${pageContext.request.contextPath}/documents">Dashboard</a>
    <a href="${pageContext.request.contextPath}/upload">Upload</a>
    <a href="${pageContext.request.contextPath}/search">Search</a>
    <a href="${pageContext.request.contextPath}/ask-notes">Ask Notes</a>
    <a href="${pageContext.request.contextPath}/revision">Revision</a>
    <a href="${pageContext.request.contextPath}/cse1007">CSE1007</a>
    <span class="spacer"></span>
    <a href="${pageContext.request.contextPath}/logout">Logout</a>
</div>
<div class="container">
    <h1 class="page-title">Ask My Notes</h1>
    <p class="page-subtitle">Strictly grounded question-answering based on your uploaded academic study materials</p>

    <div class="card">
        <form method="post" action="${pageContext.request.contextPath}/ask-notes">
            <div class="form-group">
                <label>Enter your academic question:</label>
                <input type="text" name="question" placeholder="e.g., Explain the difference between process and thread, or What are ACID properties?" value="${question}" required>
            </div>
            <button type="submit" class="btn btn-primary" style="padding:10px 24px;">Ask Question</button>
        </form>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">${errorMessage}</div>
    </c:if>

    <c:if test="${not empty answer}">
        <div class="card">
            <div class="card-title">🤖 Grounded Answer</div>
            <div style="background:#0f172a;padding:16px;border-radius:8px;border:1px solid #334155;line-height:1.7;white-space:pre-wrap;color:#f8fafc;">
                ${answer}
            </div>

            <c:if test="${not empty citations}">
                <div style="margin-top:16px;">
                    <div style="font-weight:700;color:#38bdf8;font-size:13px;margin-bottom:8px;">Referenced Sources in Your Notes:</div>
                    <ul style="padding-left:20px;color:#94a3b8;font-size:13px;">
                        <c:forEach var="cit" items="${citations}">
                            <li>${cit}</li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>
        </div>
    </c:if>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body></html>
