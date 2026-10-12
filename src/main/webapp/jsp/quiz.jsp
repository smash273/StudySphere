<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html><head>
    <title>StudySphere — Quiz</title>
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
    <h1 class="page-title">Revision Quiz</h1>
    <p class="page-subtitle">Assess your knowledge on: <strong>${quiz.topicName}</strong></p>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">${errorMessage}</div>
    </c:if>

    <c:if test="${not empty attempt}">
        <div class="card" style="border-left: 4px solid #10b981; background: #0f172a;">
            <div class="card-title">Quiz Results</div>
            <div style="font-size:24px;font-weight:800;color:#38bdf8;margin:8px 0;">
                Score: ${String.format("%.0f%%", attempt.score)} (${attempt.correctCount} / ${attempt.totalQuestions} Correct)
            </div>
            <p style="color:#94a3b8;">
                <c:choose>
                    <c:when test="${attempt.passed}">
                        🎉 Congratulations! You have demonstrated strong grasp of this topic. Topic retention updated!
                    </c:when>
                    <c:otherwise>
                        Keep going! We recommend reviewing the notes for this topic before retrying.
                    </c:otherwise>
                </c:choose>
            </p>
            <div style="margin-top:12px;">
                <a href="${pageContext.request.contextPath}/summary?topicId=${quiz.topicId}" class="btn btn-secondary">Review Topic Notes</a>
                <a href="${pageContext.request.contextPath}/revision" class="btn btn-primary" style="margin-left:8px;">View Revision Table</a>
            </div>
        </div>
    </c:if>

    <c:if test="${not empty quiz && empty attempt}">
        <form method="post" action="${pageContext.request.contextPath}/quiz">
            <input type="hidden" name="topicId" value="${quiz.topicId}">
            
            <c:forEach var="q" items="${quiz.questions}" varStatus="status">
                <input type="hidden" name="questionId" value="${q.id}">
                <div class="card">
                    <div style="font-weight:700;font-size:15px;margin-bottom:12px;color:#f8fafc;">
                        Q${status.index + 1}. ${q.questionText}
                    </div>

                    <c:if test="${q.questionType == 'TRUE_FALSE'}">
                        <label class="quiz-option">
                            <input type="radio" name="answer_${q.id}" value="True" required> True
                        </label>
                        <label class="quiz-option">
                            <input type="radio" name="answer_${q.id}" value="False" required> False
                        </label>
                    </c:if>

                    <c:if test="${q.questionType != 'TRUE_FALSE'}">
                        <c:if test="${not empty q.optionA}">
                            <label class="quiz-option">
                                <input type="radio" name="answer_${q.id}" value="${q.optionA}" required> A) ${q.optionA}
                            </label>
                        </c:if>
                        <c:if test="${not empty q.optionB}">
                            <label class="quiz-option">
                                <input type="radio" name="answer_${q.id}" value="${q.optionB}" required> B) ${q.optionB}
                            </label>
                        </c:if>
                        <c:if test="${not empty q.optionC}">
                            <label class="quiz-option">
                                <input type="radio" name="answer_${q.id}" value="${q.optionC}"> C) ${q.optionC}
                            </label>
                        </c:if>
                        <c:if test="${not empty q.optionD}">
                            <label class="quiz-option">
                                <input type="radio" name="answer_${q.id}" value="${q.optionD}"> D) ${q.optionD}
                            </label>
                        </c:if>
                    </c:if>
                </div>
            </c:forEach>

            <button type="submit" class="btn btn-success" style="padding:12px 28px;font-size:16px;">Submit Quiz</button>
        </form>
    </c:if>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body></html>
