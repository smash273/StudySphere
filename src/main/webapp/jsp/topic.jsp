<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html><head>
    <title>StudySphere — Topics</title>
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
    <h1 class="page-title">Extracted Topics</h1>
    <p class="page-subtitle">Hierarchical topics and concepts extracted from document</p>

    <div class="card">
        <div class="card-title">Topic List</div>
        <c:if test="${empty topics}">
            <p style="color:#94a3b8;">No topics found for this document.</p>
        </c:if>
        <c:if test="${not empty topics}">
            <table>
                <thead>
                    <tr>
                        <th>Topic Name</th>
                        <th>Priority</th>
                        <th>Importance</th>
                        <th>Subtopics</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="t" items="${topics}">
                        <tr>
                            <td><strong>${t.name}</strong></td>
                            <td>
                                <c:choose>
                                    <c:when test="${t.priorityLevel == 'CRITICAL'}"><span class="badge badge-danger">CRITICAL</span></c:when>
                                    <c:when test="${t.priorityLevel == 'HIGH'}"><span class="badge badge-warning">HIGH</span></c:when>
                                    <c:otherwise><span class="badge badge-info">MEDIUM</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>${String.format("%.0f%%", t.importanceScore * 100)}</td>
                            <td>${t.subtopics.size()} subtopics</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/summary?topicId=${t.id}" class="btn btn-primary" style="padding:6px 12px;font-size:12px;">View Notes</a>
                                <a href="${pageContext.request.contextPath}/quiz?topicId=${t.id}" class="btn btn-secondary" style="padding:6px 12px;font-size:12px;">Take Quiz</a>
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
