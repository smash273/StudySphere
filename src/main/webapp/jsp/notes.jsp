<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html><head>
    <title>StudySphere — Notes</title>
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
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px;">
        <div>
            <h1 class="page-title">${topic.name}</h1>
            <p class="page-subtitle">Structured academic notes, key takeaways, and formulas</p>
        </div>
        <div>
            <a href="${pageContext.request.contextPath}/quiz?topicId=${topic.id}" class="btn btn-primary">Take Revision Quiz</a>
            <form action="${pageContext.request.contextPath}/revision" method="post" style="display:inline-block;margin-left:8px;">
                <input type="hidden" name="topicId" value="${topic.id}">
                <button type="submit" class="btn btn-success">Mark as Revised</button>
            </form>
        </div>
    </div>

    <!-- Summary Card -->
    <div class="card">
        <div class="card-title">📖 Comprehensive Summary</div>
        <p style="line-height:1.7;color:#e2e8f0;white-space:pre-wrap;">${summary.summaryText}</p>
    </div>

    <!-- Important Points -->
    <div class="card">
        <div class="card-title">⭐ Key Takeaways &amp; Important Points</div>
        <c:if test="${empty importantPoints}"><p style="color:#94a3b8;">No extracted points for this topic.</p></c:if>
        <ul style="padding-left:20px;color:#cbd5e1;line-height:1.8;">
            <c:forEach var="pt" items="${importantPoints}">
                <li style="margin-bottom:6px;">${pt.pointText}</li>
            </c:forEach>
        </ul>
    </div>

    <!-- Definitions -->
    <div class="card">
        <div class="card-title">💡 Core Definitions</div>
        <c:if test="${empty definitions}"><p style="color:#94a3b8;">No definitions detected for this topic.</p></c:if>
        <c:forEach var="def" items="${definitions}">
            <div style="background:#0f172a;border-left:4px solid #38bdf8;padding:12px 16px;border-radius:6px;margin-bottom:10px;">
                <strong style="color:#38bdf8;font-size:15px;">${def.term}</strong>
                <p style="color:#cbd5e1;margin-top:4px;">${def.definitionText}</p>
            </div>
        </c:forEach>
    </div>

    <!-- Formulas -->
    <c:if test="${not empty formulas}">
    <div class="card">
        <div class="card-title">📐 Key Formulas &amp; Equations</div>
        <c:forEach var="f" items="${formulas}">
            <div style="background:#0f172a;border-left:4px solid #34d399;padding:12px 16px;border-radius:6px;margin-bottom:10px;">
                <code style="color:#34d399;font-size:16px;font-family:Consolas, monospace;">${f.expression}</code>
                <p style="color:#94a3b8;margin-top:4px;font-size:13px;">${f.explanation}</p>
            </div>
        </c:forEach>
    </div>
    </c:if>

    <!-- Keywords -->
    <div class="card">
        <div class="card-title">🏷️ Key Terminology</div>
        <div>
            <c:forEach var="kw" items="${keywords}">
                <span class="keyword-tag">${kw.keyword}</span>
            </c:forEach>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body></html>
