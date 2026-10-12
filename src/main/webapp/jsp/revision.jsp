<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html><head>
    <title>StudySphere — Revision Dashboard</title>
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
    <h1 class="page-title">Revision &amp; Retention Tracking</h1>
    <p class="page-subtitle">Spaced learning analytics and priority recommendations</p>

    <div class="card">
        <div class="card-title">Topic Revision Status</div>
        <c:if test="${empty records}">
            <p style="color:#94a3b8;">No revision records yet. Review notes or take quizzes to track your retention.</p>
        </c:if>
        <c:if test="${not empty records}">
            <table>
                <thead>
                    <tr>
                        <th>Topic</th>
                        <th>Document</th>
                        <th>Retention Score</th>
                        <th>Status</th>
                        <th>Review Count</th>
                        <th>Last Reviewed</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="r" items="${records}">
                        <tr>
                            <td><strong>${r.topicName}</strong></td>
                            <td>${r.documentTitle}</td>
                            <td>
                                <strong>${String.format("%.0f%%", r.revisionScore)}</strong>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${r.status == 'MASTERED'}"><span class="badge badge-success">MASTERED</span></c:when>
                                    <c:when test="${r.status == 'STRONG'}"><span class="badge badge-info">STRONG</span></c:when>
                                    <c:when test="${r.status == 'NEEDS_REVISION'}"><span class="badge badge-warning">NEEDS REVISION</span></c:when>
                                    <c:otherwise><span class="badge badge-danger">CRITICAL</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>${r.revisionCount}</td>
                            <td>${r.lastReviewed}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/quiz?topicId=${r.topicId}" class="btn btn-primary" style="padding:4px 10px;font-size:12px;">Quiz</a>
                                <form action="${pageContext.request.contextPath}/revision" method="post" style="display:inline-block;margin-left:4px;">
                                    <input type="hidden" name="topicId" value="${r.topicId}">
                                    <button type="submit" class="btn btn-success" style="padding:4px 10px;font-size:12px;">Mark Reviewed</button>
                                </form>
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
