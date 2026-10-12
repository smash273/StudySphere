<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html><head>
    <title>StudySphere — Dashboard</title>
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
    <h1 class="page-title">Welcome, ${sessionScope.user.fullName}</h1>
    <p class="page-subtitle">Your academic dashboard</p>

    <div class="stats-grid">
        <div class="card" style="text-align:center;"><div class="card-caption">Documents</div><div class="card-number">${totalDocuments}</div></div>
        <div class="card" style="text-align:center;"><div class="card-caption">Topics</div><div class="card-number">${totalTopics}</div></div>
        <div class="card" style="text-align:center;"><div class="card-caption">Revised</div><div class="card-number">${revisedTopics}</div></div>
        <div class="card" style="text-align:center;"><div class="card-caption">Completion</div><div class="card-number">${completionPercent}%</div></div>
    </div>

    <div class="card">
        <div class="card-title">Your Documents</div>
        <c:if test="${empty documents}"><p style="color:#64748b;">No documents uploaded yet. <a href="${pageContext.request.contextPath}/upload">Upload your first study material!</a></p></c:if>
        <table>
            <c:if test="${not empty documents}">
            <thead><tr><th>Title</th><th>Type</th><th>Topics</th><th>Uploaded</th><th>Actions</th></tr></thead>
            <tbody>
            <c:forEach var="doc" items="${documents}">
                <tr>
                    <td>${doc.title}</td>
                    <td><span class="badge badge-info">${doc.fileType}</span></td>
                    <td>${doc.totalTopics}</td>
                    <td>${doc.createdAt}</td>
                    <td><a href="${pageContext.request.contextPath}/topics?documentId=${doc.id}" class="btn btn-secondary" style="padding:6px 12px;font-size:12px;">View Topics</a></td>
                </tr>
            </c:forEach>
            </tbody>
            </c:if>
        </table>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body></html>
