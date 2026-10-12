<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html><head>
    <title>StudySphere — Register</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head><body>
<div class="navbar"><span class="brand">StudySphere</span><span class="spacer"></span>
    <a href="${pageContext.request.contextPath}/login">Login</a>
    <a href="${pageContext.request.contextPath}/register">Register</a>
</div>
<div class="auth-wrapper"><div class="card auth-card">
    <h2 style="text-align:center;margin-bottom:8px;">Student Registration</h2>
    <p style="text-align:center;color:#94a3b8;margin-bottom:24px;">Create your StudySphere account</p>
    <c:if test="${not empty errorMessage}"><div class="alert alert-error">${errorMessage}</div></c:if>
    <form method="post" action="${pageContext.request.contextPath}/register" data-validate="true">
        <div class="form-group"><label>Full Name</label><input type="text" name="fullName" required></div>
        <div class="form-group"><label>Username</label><input type="text" name="username" required></div>
        <div class="form-group"><label>Email</label><input type="email" name="email" required></div>
        <div class="form-group"><label>Password</label><input type="password" name="password" required></div>
        <div class="form-group"><label>Student ID</label><input type="text" name="studentId" required></div>
        <div class="form-group"><label>Enrollment No</label><input type="text" name="enrollmentNo" required></div>
        <div class="form-group"><label>Major</label><input type="text" name="major" required></div>
        <div class="form-group"><label>Semester</label><input type="number" name="semester" min="1" max="10" value="5" required></div>
        <button type="submit" class="btn btn-success" style="width:100%;padding:12px;">Create Account</button>
    </form>
    <p style="text-align:center;margin-top:16px;"><a href="${pageContext.request.contextPath}/login">Already have an account? Sign in</a></p>
</div></div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body></html>
