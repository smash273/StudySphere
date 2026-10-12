<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html><head>
    <title>StudySphere — Upload</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head><body>
<div class="navbar">
    <span class="brand">StudySphere</span>
    <a href="${pageContext.request.contextPath}/documents">Dashboard</a>
    <a href="${pageContext.request.contextPath}/upload">Upload</a>
    <a href="${pageContext.request.contextPath}/search">Search</a>
    <a href="${pageContext.request.contextPath}/ask-notes">Ask Notes</a>
    <a href="${pageContext.request.contextPath}/revision">Revision</a>
    <span class="spacer"></span>
    <a href="${pageContext.request.contextPath}/logout">Logout</a>
</div>
<div class="container">
    <h1 class="page-title">Upload Study Material</h1>
    <p class="page-subtitle">Upload PDF, DOCX, or TXT files for AI-powered analysis</p>
    <c:if test="${not empty successMessage}"><div class="alert alert-success">${successMessage}</div></c:if>
    <c:if test="${not empty errorMessage}"><div class="alert alert-error">${errorMessage}</div></c:if>
    <div class="card" style="max-width:600px;">
        <div class="card-title">Document Upload</div>
        <form method="post" action="${pageContext.request.contextPath}/upload" enctype="multipart/form-data" data-validate="true">
            <div class="form-group"><label>Title (optional)</label><input type="text" name="title" placeholder="Auto-detected from filename"></div>
            <div class="form-group"><label>Select File (PDF, DOCX, TXT — max 50MB)</label><input type="file" name="file" accept=".pdf,.docx,.txt" required></div>
            <button type="submit" class="btn btn-primary" style="width:100%;padding:12px;">Upload &amp; Analyze with AI</button>
        </form>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body></html>
