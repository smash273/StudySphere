<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html><head>
    <title>StudySphere — Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head><body>
<div class="navbar"><span class="brand">StudySphere</span><span class="spacer"></span>
    <a href="${pageContext.request.contextPath}/login">Login</a>
    <a href="${pageContext.request.contextPath}/register">Register</a>
</div>
<div class="auth-wrapper"><div class="card auth-card">
    <h2 style="text-align:center;margin-bottom:8px;">Sign In</h2>
    <p style="text-align:center;color:#94a3b8;margin-bottom:24px;">AI-Powered Learning Platform</p>
    <c:if test="${not empty errorMessage}"><div class="alert alert-error">${errorMessage}</div></c:if>
    <c:if test="${not empty successMessage}"><div class="alert alert-success">${successMessage}</div></c:if>
    <form method="post" action="${pageContext.request.contextPath}/login" data-validate="true">
        <div class="form-group"><label>Username</label><input type="text" name="username" required value="student"></div>
        <div class="form-group"><label>Password</label><input type="password" name="password" required value="password123"></div>
        <button type="submit" class="btn btn-primary" style="width:100%;padding:12px;">Sign In</button>
    </form>
    <p style="text-align:center;margin-top:16px;"><a href="${pageContext.request.contextPath}/register">Don't have an account? Register</a></p>
</div></div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body></html>
