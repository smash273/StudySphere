# CSE1007 Java Programming — Syllabus & Concept Mapping Document

This document details the concrete, meaningful implementation of every core Java programming concept evaluated in **CSE1007**.

---

## 1. Object-Oriented Programming (OOP)

### Classes, Objects & Encapsulation
- All domain entities reside in `com.studysphere.model`.
- Every class enforces information hiding via `private` instance variables with explicit getters, setters, and constructors.
- Validation checks are enforced during property mutations (e.g., student semester ranges, non-negative scores).

### Inheritance
- **StudyMaterial Hierarchy**:
  ```
  StudyMaterial (Abstract Base Class)
  ├── PDFMaterial (page count, PDFBox metadata)
  ├── DOCXMaterial (word count, section metadata)
  └── TextMaterial (line count, character encoding)
  ```
- **User Hierarchy**:
  ```
  User (Base Class: id, username, email, passwordHash, role)
  └── Student (Subclass: studentId, enrollmentNo, major, semester)
  ```

### Polymorphism & Dynamic Method Dispatch
- `DocumentParser` interface provides the abstract method `String parse(InputStream input)`.
- `PDFDocumentParser`, `DOCXDocumentParser`, and `TextDocumentParser` override `parse()` with format-specific parsing algorithms.
- `DocumentParserFactory` instantiates the appropriate subclass dynamically at runtime based on file extension.
- Client services invoke `parser.parse(stream)` uniformly without coupling to concrete implementations.

### Abstraction & Interfaces
- Generic persistence contract defined by `Repository<T>`:
  - `save(T entity)`
  - `findById(int id)`
  - `findAll()`
  - `update(T entity)`
  - `deleteById(int id)`
- AI capabilities encapsulated behind `AIProvider` and `Summarizer` interfaces, with `LLMAPIProvider` and `FallbackAIProvider` implementations.

---

## 2. Exception Handling & Custom Exceptions

A clean, hierarchical exception taxonomy under `com.studysphere.exception`:
- `StudySphereException` (Base checked exception)
  - `AIServiceException`: Raised during AI API failures, prompt timeout, or JSON parse errors.
  - `DatabaseException`: Wraps low-level SQLExceptions to insulate business logic.
  - `InvalidFileException`: Triggered on empty, corrupted, or oversized file uploads.
  - `UnsupportedFileException`: Triggered when an unrecognized extension is supplied.
  - `AuthenticationException`: Triggered on invalid credentials or session expiration.
  - `ValidationException`: Triggered when form input invariants are violated.

Robust `try-with-resources` blocks ensure zero resource leaks across all JDBC statements and file input streams.

---

## 3. Collections Framework

- `java.util.List` / `ArrayList`: Used across DAO query result sets and topic compositions.
- `java.util.Map` / `HashMap`: Used for metadata properties and dictionary term resolution.
- `java.util.PriorityQueue`: Used in `RevisionService.getRecommendedRevisionTopics()` with a custom `Comparator<Topic>` based on retention score and topic priority level.
- `java.util.concurrent.ConcurrentHashMap`: Employed in `ProcessingJobQueue` to track asynchronous document processing jobs safely across thread boundaries.

---

## 4. Multithreading & Concurrency

- `ThreadPoolManager`: Singleton managing a cached/fixed `ExecutorService` thread pool.
- `DocumentProcessingTask`: Implements `Callable<AIResponse>` allowing documents to be ingested, chunked, and summarized in background worker threads.
- `CompletableFuture<Document>`: Used in `DocumentService.uploadAndProcessAsync()` to enable non-blocking UI notifications via `Platform.runLater()`.
- Thread safety achieved via immutable task states and synchronized queue structures.

---

## 5. JDBC & Database Persistence

- Pure JDBC implementation with zero heavyweight ORM abstraction.
- Connection pooling managed via `com.zaxxer.hikari.HikariDataSource`.
- 100% of queries use `PreparedStatement` with parameterized placeholders (`?`) to prevent SQL injection.
- Relational schema encompasses 15 tables with foreign keys, cascading deletions, and secondary indexes.

---

## 6. Dual-Client MVC Implementation

### JavaFX Desktop Client
- Clean separation: FXML (View) + Controllers + Model.
- Scene navigation governed by `ViewNavigator`.
- Non-blocking background worker threads communicate with JavaFX UI thread via `Platform.runLater()`.

### Jakarta Servlet & JSP Web Client
- Controllers implemented as Jakarta Servlets (`@WebServlet`).
- Standard MVC request-response cycle: Servlets receive HTTP requests, call Service Layer, store results in request scope, and forward to JSP views via `RequestDispatcher`.
- State preserved across requests using `HttpSession`.
