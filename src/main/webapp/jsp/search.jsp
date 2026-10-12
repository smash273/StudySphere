<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html><head>
    <title>StudySphere — Search Notes</title>
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
    <h1 class="page-title">Search Study Notes</h1>
    <p class="page-subtitle">Find concepts, definitions, formulas and summaries across all your materials</p>

    <div class="card">
        <form method="get" action="${pageContext.request.contextPath}/search">
            <div style="display:flex;gap:12px;">
                <input type="text" name="q" placeholder="Enter keywords, e.g., deadlock, ACID, polymorphism..." value="${query}" required style="flex:1;">
                <button type="submit" class="btn btn-primary">Search Notes</button>
            </div>
        </form>
    </div>

    <c:if test="${not empty query}">
        <div class="card">
            <div class="card-title">Search Results for "${query}" (${results.size()} found)</div>
            <c:if test="${empty results}">
                <p style="color:#94a3b8;">No matching topics or notes found. Try different search terms or check spelling.</p>
            </c:if>
            <c:if test="${not empty results}">
                <table>
                    <thead>
                        <tr>
                            <th>Topic</th>
                            <th>Priority</th>
                            <th>Overview</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="t" items="${results}">
                            <tr>
                                <td><strong>${t.name}</strong></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${t.priorityLevel == 'CRITICAL'}"><span class="badge badge-danger">CRITICAL</span></c:when>
                                        <c:when test="${t.priorityLevel == 'HIGH'}"><span class="badge badge-warning">HIGH</span></c:when>
                                        <c:otherwise><span class="badge badge-info">MEDIUM</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="color:#cbd5e1;max-width:400px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">${t.overview}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/summary?topicId=${t.id}" class="btn btn-secondary" style="padding:6px 12px;font-size:12px;">Open Notes</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </div>
    </c:if>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body></html>
