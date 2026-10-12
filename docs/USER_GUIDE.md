# StudySphere — User & Evaluator Guide

## 1. System Requirements & Preparation

- **Operating System**: Windows 10 / 11, macOS, or Linux.
- **Java Development Kit**: JDK 21+ (`C:\Users\Rajde\tools\jdk-21.0.6+7`).
- **Build Tool**: Apache Maven 3.9+ (`C:\Users\Rajde\tools\apache-maven-3.9.6`).
- **Database**: PostgreSQL 18 running on `localhost:5432` with database `studysphere`.

---

## 2. Launching the Application

### Option A: Desktop Client (Recommended for Viva / Evaluation)
Run `run-desktop.bat` from the project root or execute:
```cmd
mvn javafx:run
```
Login using the pre-seeded account:
- **Username**: `student`
- **Password**: `password123`

### Option B: Web Application
Build the WAR archive:
```cmd
mvn clean package -DskipTests
```
Deploy `target/studysphere.war` to Apache Tomcat 10+ and visit:
```
http://localhost:8080/studysphere/login
```

---

## 3. Core Functional Walkthrough

### 1. Document Upload & Multithreaded Processing
- Navigate to **Upload Material**.
- Choose any academic document (`.pdf`, `.docx`, or `.txt`).
- Ready-to-use sample notes are provided in `samples/`:
  - `operating_systems_notes.txt`
  - `database_management_notes.txt`
  - `java_programming_notes.txt`
- Click **Upload & Analyze with AI**.
- Watch real-time stage progression as background worker threads chunk, parse, and analyze the document without freezing the UI.

### 2. Academic Notes Viewer
- Browse topics and subtopics in the left-hand hierarchical `TreeView`.
- Inspect detailed sections:
  - **Summary**: Concise, factual recap.
  - **Key Takeaways**: Bulleted core points.
  - **Definitions**: Highlighted terminology cards.
  - **Formulas**: Rendered mathematical expressions.
  - **Keywords**: Tagged concepts.
- Click **Mark as Revised** to update retention scores in PostgreSQL.
- Click **Copy Notes** to export to system clipboard.

### 3. Ask My Notes (Grounded Q&A)
- Navigate to **Ask My Notes**.
- Ask questions strictly evaluated against your uploaded documents (e.g., *"What are Coffman conditions for deadlock?"*).
- Observe exact cited sources from your notes.

### 4. Revision & Spaced Practice Quizzes
- View retention analytics and priority queues under **Revision**.
- Select any topic and take an interactive 5-question quiz to assess mastery.
- Score and feedback are dynamically calculated and recorded in the database.

### 5. CSE1007 Demonstration Mode
- Open **CSE1007 Demo** from the navigation bar.
- Review the live concept verification table, displaying active database connectivity, executor pool status, and curriculum alignments.
