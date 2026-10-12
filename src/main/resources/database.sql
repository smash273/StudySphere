-- ====================================================================
-- StudySphere Relational Database Schema (PostgreSQL)
-- CSE1007 Java Programming Project Schema
-- ====================================================================

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Students Table (Demonstrating OOP inheritance persistence)
CREATE TABLE IF NOT EXISTS students (
    user_id INTEGER PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    student_id VARCHAR(30) UNIQUE,
    enrollment_no VARCHAR(30),
    major VARCHAR(100),
    semester INTEGER DEFAULT 1,
    overall_score DOUBLE PRECISION DEFAULT 0.0
);

-- 3. Documents Table
CREATE TABLE IF NOT EXISTS documents (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    file_type VARCHAR(10) NOT NULL, -- PDF, DOCX, TXT
    file_size_bytes BIGINT NOT NULL,
    storage_path VARCHAR(500) NOT NULL,
    status VARCHAR(30) DEFAULT 'QUEUED', -- QUEUED, PROCESSING, COMPLETED, FAILED
    total_chunks INTEGER DEFAULT 0,
    total_topics INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Document Chunks Table
CREATE TABLE IF NOT EXISTS document_chunks (
    id SERIAL PRIMARY KEY,
    document_id INTEGER REFERENCES documents(id) ON DELETE CASCADE,
    chunk_index INTEGER NOT NULL,
    start_char INTEGER NOT NULL,
    end_char INTEGER NOT NULL,
    chunk_text TEXT NOT NULL,
    token_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Topics Table
CREATE TABLE IF NOT EXISTS topics (
    id SERIAL PRIMARY KEY,
    document_id INTEGER REFERENCES documents(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    overview TEXT,
    importance_score DOUBLE PRECISION DEFAULT 0.5, -- 0.0 to 1.0
    priority_level VARCHAR(20) DEFAULT 'MEDIUM',   -- LOW, MEDIUM, HIGH, CRITICAL
    order_index INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. Subtopics Table
CREATE TABLE IF NOT EXISTS subtopics (
    id SERIAL PRIMARY KEY,
    topic_id INTEGER REFERENCES topics(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    content TEXT,
    order_index INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. Summaries Table
CREATE TABLE IF NOT EXISTS summaries (
    id SERIAL PRIMARY KEY,
    document_id INTEGER REFERENCES documents(id) ON DELETE CASCADE,
    topic_id INTEGER REFERENCES topics(id) ON DELETE CASCADE,
    summary_type VARCHAR(30) DEFAULT 'TOPIC', -- DOCUMENT, TOPIC, EXECUTIVE
    summary_text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 8. Important Points Table
CREATE TABLE IF NOT EXISTS important_points (
    id SERIAL PRIMARY KEY,
    topic_id INTEGER REFERENCES topics(id) ON DELETE CASCADE,
    subtopic_id INTEGER REFERENCES subtopics(id) ON DELETE SET NULL,
    point_text TEXT NOT NULL,
    weight DOUBLE PRECISION DEFAULT 1.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 9. Keywords Table
CREATE TABLE IF NOT EXISTS keywords (
    id SERIAL PRIMARY KEY,
    topic_id INTEGER REFERENCES topics(id) ON DELETE CASCADE,
    keyword VARCHAR(100) NOT NULL,
    relevance_score DOUBLE PRECISION DEFAULT 0.5,
    occurrence_count INTEGER DEFAULT 1
);

-- 10. Definitions Table
CREATE TABLE IF NOT EXISTS definitions (
    id SERIAL PRIMARY KEY,
    topic_id INTEGER REFERENCES topics(id) ON DELETE CASCADE,
    term VARCHAR(150) NOT NULL,
    definition_text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 11. Formulas Table
CREATE TABLE IF NOT EXISTS formulas (
    id SERIAL PRIMARY KEY,
    topic_id INTEGER REFERENCES topics(id) ON DELETE CASCADE,
    expression VARCHAR(255) NOT NULL,
    explanation TEXT,
    variables_json TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 12. Revision Records Table
CREATE TABLE IF NOT EXISTS revision_records (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    topic_id INTEGER REFERENCES topics(id) ON DELETE CASCADE,
    revision_count INTEGER DEFAULT 0,
    last_reviewed TIMESTAMP,
    importance_weight DOUBLE PRECISION DEFAULT 1.0,
    recency_factor DOUBLE PRECISION DEFAULT 1.0,
    revision_score DOUBLE PRECISION DEFAULT 0.0,
    status VARCHAR(30) DEFAULT 'NEEDS_REVISION', -- NEEDS_REVISION, RECENTLY_REVISED, STRONG, MASTERED
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_topic_revision UNIQUE (user_id, topic_id)
);

-- 13. Processing Jobs Table (Multithreaded Task Tracking)
CREATE TABLE IF NOT EXISTS processing_jobs (
    id VARCHAR(64) PRIMARY KEY,
    document_id INTEGER REFERENCES documents(id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(30) NOT NULL, -- QUEUED, EXTRACTING, ANALYZING, SAVING, COMPLETED, FAILED
    progress_percent INTEGER DEFAULT 0,
    current_stage VARCHAR(100),
    error_message TEXT,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

-- 14. Quiz Questions Table
CREATE TABLE IF NOT EXISTS quiz_questions (
    id SERIAL PRIMARY KEY,
    topic_id INTEGER REFERENCES topics(id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    question_type VARCHAR(20) DEFAULT 'MCQ', -- MCQ, TRUE_FALSE, SHORT_ANSWER
    option_a VARCHAR(255),
    option_b VARCHAR(255),
    option_c VARCHAR(255),
    option_d VARCHAR(255),
    correct_answer VARCHAR(255) NOT NULL,
    explanation TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 15. Quiz Attempts Table
CREATE TABLE IF NOT EXISTS quiz_attempts (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    topic_id INTEGER REFERENCES topics(id) ON DELETE CASCADE,
    score DOUBLE PRECISION NOT NULL,
    total_questions INTEGER NOT NULL,
    correct_count INTEGER NOT NULL,
    attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for Fast Query Performance
CREATE INDEX IF NOT EXISTS idx_documents_user ON documents(user_id);
CREATE INDEX IF NOT EXISTS idx_chunks_document ON document_chunks(document_id);
CREATE INDEX IF NOT EXISTS idx_topics_document ON topics(document_id);
CREATE INDEX IF NOT EXISTS idx_subtopics_topic ON subtopics(topic_id);
CREATE INDEX IF NOT EXISTS idx_summaries_topic ON summaries(topic_id);
CREATE INDEX IF NOT EXISTS idx_points_topic ON important_points(topic_id);
CREATE INDEX IF NOT EXISTS idx_keywords_topic ON keywords(topic_id);
CREATE INDEX IF NOT EXISTS idx_definitions_topic ON definitions(topic_id);
CREATE INDEX IF NOT EXISTS idx_formulas_topic ON formulas(topic_id);
CREATE INDEX IF NOT EXISTS idx_revision_user_topic ON revision_records(user_id, topic_id);
CREATE INDEX IF NOT EXISTS idx_quiz_questions_topic ON quiz_questions(topic_id);
