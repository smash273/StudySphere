<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html><head>
    <title>StudySphere — Documents</title>
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
    <h1 class="page-title">My Study Materials</h1>
    <p class="page-subtitle">All processed academic documents and their extracted knowledge structures</p>

    <div class="card">
        <div class="card-title">Document Repository</div>
        <c:if test="${empty documents}">
            <p style="color:#94a3b8;">No documents found. <a href="${pageContext.request.contextPath}/upload">Upload study material</a> to get started.</p>
        </c:if>
        <c:if test="${not empty documents}">
            <table>
                <thead>
                    <tr>
                        <th>Title</th>
                        <th>Type</th>
                        <th>Size</th>
                        <th>Topics</th>
                        <th>Processed Date</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="doc" items="${documents}">
                        <tr>
                            <td><strong>${doc.title}</strong><br><span style="font-size:12px;color:#94a3b8;">${doc.originalFilename}</span></td>
                            <td><span class="badge badge-info">${doc.fileType}</span></td>
                            <td>${String.format("%.1f KB", doc.fileSizeBytes / 1024.0)}</td>
                            <td><strong>${doc.totalTopics}</strong></td>
                            <td>${doc.createdAt}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/topics?documentId=${doc.id}" class="btn btn-primary" style="padding:6px 12px;font-size:12px;">Browse Topics</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body></html>
