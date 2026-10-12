<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html><head>
    <title>StudySphere — Error</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head><body>
<div class="navbar">
    <span class="brand">StudySphere</span>
    <a href="${pageContext.request.contextPath}/documents">Dashboard</a>
    <span class="spacer"></span>
    <a href="${pageContext.request.contextPath}/login">Login</a>
</div>
<div class="container" style="max-width:600px;margin-top:60px;">
    <div class="card" style="border-left: 4px solid #ef4444;">
        <h2 style="color:#ef4444;margin-bottom:12px;">Notice / Error</h2>
        <p style="color:#f8fafc;font-size:15px;line-height:1.6;">
            <c:choose>
                <c:when test="${not empty errorMessage}">${errorMessage}</c:when>
                <c:when test="${not empty pageContext.exception}">${pageContext.exception.message}</c:when>
                <c:otherwise>An unexpected issue occurred or the requested resource was not found.</c:otherwise>
            </c:choose>
        </p>
        <div style="margin-top:20px;">
            <a href="${pageContext.request.contextPath}/documents" class="btn btn-primary">Return to Dashboard</a>
            <a href="javascript:history.back()" class="btn btn-secondary" style="margin-left:8px;">Go Back</a>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body></html>
