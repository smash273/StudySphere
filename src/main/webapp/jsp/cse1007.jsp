<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html><head>
    <title>StudySphere — CSE1007 Technical Demo</title>
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
    <h1 class="page-title">CSE1007 — Java Programming Syllabus Compliance</h1>
    <p class="page-subtitle">Academic assessment and technical concept verification for StudySphere</p>

    <div class="card" style="margin-bottom:20px;">
        <div class="card-title">Live Infrastructure Health</div>
        <p>Database Status: 
            <c:choose>
                <c:when test="${dbOk}"><strong style="color:#10b981;">CONNECTED (PostgreSQL 18 + HikariCP)</strong></c:when>
                <c:otherwise><strong style="color:#ef4444;">DISCONNECTED</strong></c:otherwise>
            </c:choose>
        </p>
    </div>

    <div class="card">
        <div class="card-title">Java Concepts &amp; Architecture Matrix</div>
        <table>
            <thead>
                <tr>
                    <th>Concept</th>
                    <th>Implementation in StudySphere</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><strong>Classes &amp; Objects</strong></td>
                    <td>Domain entities: <code>User, Student, Document, Topic, SubTopic, Summary, ImportantPoint, Quiz</code></td>
                    <td><span class="badge badge-success">VERIFIED</span></td>
                </tr>
                <tr>
                    <td><strong>Inheritance &amp; Polymorphism</strong></td>
                    <td><code>StudyMaterial &gt; PDFMaterial, DOCXMaterial, TextMaterial</code>; <code>User &gt; Student</code>; <code>DocumentParser</code> hierarchy</td>
                    <td><span class="badge badge-success">VERIFIED</span></td>
                </tr>
                <tr>
                    <td><strong>Abstraction &amp; Interfaces</strong></td>
                    <td><code>DocumentParser, Summarizer, TopicExtractor, Repository&lt;T&gt;, AIProvider</code></td>
                    <td><span class="badge badge-success">VERIFIED</span></td>
                </tr>
                <tr>
                    <td><strong>Exception Handling</strong></td>
                    <td>Hierarchy: <code>StudySphereException, AIServiceException, DatabaseException, InvalidFileException</code></td>
                    <td><span class="badge badge-success">VERIFIED</span></td>
                </tr>
                <tr>
                    <td><strong>Collections Framework</strong></td>
                    <td><code>ArrayList, HashMap, PriorityQueue</code> (revision prioritization), <code>ConcurrentHashMap</code> (job queues)</td>
                    <td><span class="badge badge-success">VERIFIED</span></td>
                </tr>
                <tr>
                    <td><strong>Multithreading &amp; Concurrency</strong></td>
                    <td><code>ThreadPoolManager (ExecutorService), DocumentProcessingTask (Callable), CompletableFuture</code></td>
                    <td><span class="badge badge-success">VERIFIED</span></td>
                </tr>
                <tr>
                    <td><strong>JDBC &amp; DAO Pattern</strong></td>
                    <td><code>HikariDataSource, PreparedStatement, BaseDAO, UserDAO, DocumentDAO, TopicDAO, QuizDAO</code></td>
                    <td><span class="badge badge-success">VERIFIED</span></td>
                </tr>
                <tr>
                    <td><strong>Servlets &amp; JSP</strong></td>
                    <td>Jakarta Servlet 6.0 controllers with session management, JSTL templates, request dispatching</td>
                    <td><span class="badge badge-success">VERIFIED</span></td>
                </tr>
                <tr>
                    <td><strong>JavaFX Desktop GUI</strong></td>
                    <td>FXML controllers, custom CSS, scene navigation, dynamic tree and card views</td>
                    <td><span class="badge badge-success">VERIFIED</span></td>
                </tr>
                <tr>
                    <td><strong>File Handling &amp; Parsing</strong></td>
                    <td>Apache PDFBox (PDF), Apache POI (DOCX), Java NIO (TXT), file uploads with size validation</td>
                    <td><span class="badge badge-success">VERIFIED</span></td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body></html>
