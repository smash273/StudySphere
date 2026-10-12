# StudySphere — AI-Powered Academic Study Material Analysis & Personalized Learning Platform

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Maven 3.9](https://img.shields.io/badge/Maven-3.9-blue.svg)](https://maven.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL_18-336791.svg)](https://www.postgresql.org/)
[![JavaFX 21](https://img.shields.io/badge/GUI-JavaFX_21-green.svg)](https://openjfx.io/)
[![Jakarta Servlet](https://img.shields.io/badge/Web-Jakarta_Servlet_6.0-red.svg)](https://jakarta.ee/)
[![Course](https://img.shields.io/badge/Course-CSE1007_Java_Programming-purple.svg)]()

> **StudySphere** is an AI-powered student learning platform designed to demonstrate concepts required by **CSE1007 – Java Programming**. The platform automatically ingests academic study material (PDF, DOCX, TXT), performs multithreaded text extraction and chunking, generates structured summaries, extracts key definitions/formulas/keywords, provides strictly grounded Q&A, and tracks spaced retention using priority queues and relational persistence.

---

## 🌟 Key Features

1. **Document Ingestion & Parsing**: Native parsers for PDF (Apache PDFBox 3.0), Word (Apache POI 5.2), and Plain Text (Java NIO), with mime-type and size validation.
2. **AI Analysis Pipeline**: Flexible provider architecture supporting OpenAI, Gemini, and a deterministic local Fallback AI engine that operates completely offline.
3. **Structured Academic Notes**: Automatic extraction of topic hierarchies, subtopics, summaries, key takeaways, definitions, and mathematical equations.
4. **Strictly Grounded Q&A ("Ask My Notes")**: Context-bounded question answering citing uploaded document sources without hallucination.
5. **Spaced Revision & Quiz Engine**: Priority-queue-based revision recommendation algorithm and interactive assessment quizzes.
6. **Dual-Client Architecture**:
   - **Desktop**: Native JavaFX 21 application with modern dark-themed CSS and interactive Scene navigation.
   - **Web**: Jakarta Servlet 6.0 and JSP with JSTL templates ready for deployment on Apache Tomcat 10+.

---

## 📚 CSE1007 Java Programming Curriculum Mapping

| Java Concept | Project Implementation |
|---|---|
| **Classes & Objects** | Domain models: `User`, `Student`, `Document`, `Topic`, `SubTopic`, `Summary`, `Quiz` |
| **Encapsulation** | Strict private fields with validated getters and setters across all entity classes |
| **Inheritance** | `StudyMaterial` base class extended by `PDFMaterial`, `DOCXMaterial`, `TextMaterial`; `User` extended by `Student` |
| **Polymorphism** | `DocumentParser` implementations resolved dynamically via `DocumentParserFactory`; `AIProvider` strategies |
| **Abstraction & Interfaces** | `Repository<T>`, `DocumentParser`, `Summarizer`, `AIProvider`, `ProgressCallback` |
| **Exception Handling** | Custom hierarchy rooted at `StudySphereException` (`AIServiceException`, `DatabaseException`, `InvalidFileException`) |
| **Collections Framework** | `ArrayList`, `HashMap`, `PriorityQueue` (spaced revision scoring), `ConcurrentHashMap` (job queue), `TreeMap` |
| **Multithreading** | `ThreadPoolManager` (`ExecutorService`), `DocumentProcessingTask` (`Callable<AIResponse>`), `CompletableFuture` |
| **File I/O & Streams** | `FileInputStream`, `Files.readString`, `Loader.loadPDF` (PDFBox 3), `XWPFDocument` (POI 5) |
| **JDBC & DAO Pattern** | HikariCP connection pooling, `PreparedStatement` parameterization, `ResultSet` mapping, `BaseDAO` pattern |
| **JavaFX GUI** | 10 FXML layouts, `ViewNavigator`, `Platform.runLater` thread synchronization, custom CSS theme |
| **Servlets & JSP** | Jakarta Servlet 6.0 controllers (`@WebServlet`), session tracking (`HttpSession`), JSTL EL views |
| **MVC Architecture** | Model (entities), View (FXML/JSP), Controller (`DashboardController`, `UploadServlet`) |

---

## 🛠️ Technology Stack

- **Backend**: Java 21 LTS, Maven 3.9+
- **Persistence**: PostgreSQL 18, HikariCP 5.1.0 connection pool
- **Desktop UI**: JavaFX 21 (Controls, FXML, Graphics)
- **Web UI**: Jakarta Servlet 6.0, Jakarta JSP 3.1, JSTL 3.0
- **Document Parsers**: Apache PDFBox 3.0.1, Apache POI 5.2.5
- **Security**: jBCrypt for salted password hashing
- **JSON & Logging**: Jackson 2.17, SLF4J 2.0 + Logback 1.5
- **Testing**: JUnit 5 (Jupiter)

---

## 🚀 Quick Start Guide

### Prerequisites
1. **Java JDK 21+** configured in environment path.
2. **Apache Maven 3.9+** installed.
3. **PostgreSQL 18** installed with database named `studysphere`.

### 1. Database Setup
```bash
# Connect to PostgreSQL and create database
psql -U postgres -c "CREATE DATABASE studysphere;"

# Apply schema and initial sample academic data
psql -U postgres -d studysphere -f src/main/resources/database.sql
psql -U postgres -d studysphere -f src/main/resources/sample-data.sql
```

### 2. Launching Desktop Application (JavaFX)
Simply double click `run-desktop.bat` or execute:
```bash
mvn javafx:run
```
*Pre-configured demo credentials:*
- **Username**: `student`
- **Password**: `password123`

### 3. Packaging & Running Web Application (Jakarta Servlet/JSP)
Double click `run-web.bat` or execute:
```bash
mvn clean package -DskipTests
```
Deploy the resulting `target/studysphere.war` to Apache Tomcat 10+ `webapps/` directory and navigate to `http://localhost:8080/studysphere/`.

---

## 📁 Project Directory Structure

```
StudySphere/
├── pom.xml
├── README.md
├── run-desktop.bat
├── run-web.bat
├── samples/                      # Comprehensive academic study materials
│   ├── operating_systems_notes.txt
│   ├── database_management_notes.txt
│   └── java_programming_notes.txt
├── src/
│   ├── main/
│   │   ├── java/com/studysphere/
│   │   │   ├── ai/               # AIProvider, LLMAPIProvider, FallbackAIProvider
│   │   │   ├── concurrency/      # ThreadPoolManager, ProcessingJobQueue
│   │   │   ├── config/           # DatabaseConnectionManager, DatabaseConfig
│   │   │   ├── dao/              # UserDAO, DocumentDAO, TopicDAO, QuizDAO...
│   │   │   ├── document/         # DocumentParser, PDF, DOCX, TXT parsers, TextChunker
│   │   │   ├── exception/        # Custom exception hierarchy
│   │   │   ├── javafx/           # JavaFX controllers and ViewNavigator
│   │   │   ├── model/            # Domain models, inheritance hierarchies, enums
│   │   │   ├── service/          # AuthService, DocumentService, RevisionService...
│   │   │   └── servlet/          # Jakarta Servlets
│   │   ├── resources/
│   │   │   ├── application.properties
│   │   │   ├── database.sql
│   │   │   ├── sample-data.sql
│   │   │   └── com/studysphere/javafx/  # FXML screens and CSS stylesheet
│   │   └── webapp/
│   │       ├── css/ & js/
│   │       ├── jsp/              # 12 Jakarta JSP templates
│   │       └── WEB-INF/web.xml
│   └── test/java/com/studysphere/  # JUnit 5 automated test suite
```

---

## 🧪 Running Tests

To run the automated JUnit 5 test suite:
```bash
mvn test
```
All unit tests verify parser functionality, chunking behavior, deterministic NLP, and OOP inheritance integrity.
