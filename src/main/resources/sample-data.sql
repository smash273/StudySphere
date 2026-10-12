-- ====================================================================
-- StudySphere Sample Academic Data (PostgreSQL)
-- Provides instant demo data for Operating Systems, DBMS & Java Programming
-- Default User: student / password123 (BCrypt Hash: $2a$10$wN1q0/U5k5wO1Jv49k78iO7B8Kj1nK1/k8K2eXQy7Wf7VzH8B9Fqu)
-- ====================================================================

-- 1. Insert Sample Student User
INSERT INTO users (id, username, email, password_hash, full_name, role)
VALUES (1, 'student', 'student@studysphere.edu', '$2a$10$4n9P7R3KjL2M1q0w8e9r0uYhT6g5f4d3s2a1z0x9c8v7b6n5m4l3k', 'Alex Rivera', 'STUDENT')
ON CONFLICT (id) DO UPDATE SET username = EXCLUDED.username;

INSERT INTO students (user_id, student_id, enrollment_no, major, semester, overall_score)
VALUES (1, 'STU-2026-001', '21BCE1007', 'Computer Science & Engineering', 5, 87.5)
ON CONFLICT (user_id) DO NOTHING;

-- 2. Insert Sample Documents
INSERT INTO documents (id, user_id, title, original_filename, file_type, file_size_bytes, storage_path, status, total_chunks, total_topics)
VALUES 
(1, 1, 'Operating Systems: Concurrency & Deadlocks', 'os_concurrency_notes.pdf', 'PDF', 142850, './uploads/os_concurrency_notes.pdf', 'COMPLETED', 4, 3),
(2, 1, 'Database Management: Normalization & Transactions', 'dbms_transactions.docx', 'DOCX', 98400, './uploads/dbms_transactions.docx', 'COMPLETED', 3, 2),
(3, 1, 'Java Programming: Concurrency & Collections', 'java_advanced_notes.txt', 'TXT', 65200, './uploads/java_advanced_notes.txt', 'COMPLETED', 2, 2)
ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title;

-- 3. Insert Topics for Document 1 (Operating Systems)
INSERT INTO topics (id, document_id, name, overview, importance_score, priority_level, order_index)
VALUES 
(1, 1, 'Process Management & Synchronization', 'Fundamental concepts of processes, threads, race conditions, and synchronization primitives like semaphores and mutexes.', 0.95, 'CRITICAL', 1),
(2, 1, 'Deadlock Characterization & Prevention', 'The four Coffman conditions necessary for deadlock, deadlock prevention strategies, and resource allocation graphs.', 0.90, 'HIGH', 2),
(3, 1, 'CPU Scheduling Algorithms', 'Comparison of preemptive and non-preemptive scheduling algorithms including FCFS, SJF, Priority, and Round Robin.', 0.85, 'HIGH', 3),

-- Topics for Document 2 (DBMS)
(4, 2, 'Relational Normalization', 'Rules for organizing database tables to reduce redundancy and eliminate update, insertion, and deletion anomalies (1NF to BCNF).', 0.92, 'CRITICAL', 1),
(5, 2, 'ACID Properties & Transaction Concurrency', 'Atomicity, Consistency, Isolation, and Durability, with two-phase locking and serializability.', 0.88, 'HIGH', 2),

-- Topics for Document 3 (Java Programming)
(6, 3, 'Java Collections Framework', 'Comprehensive hierarchy of List, Set, Queue, and Map interfaces with their performance trade-offs.', 0.96, 'CRITICAL', 1),
(7, 3, 'Multithreading & Concurrency Utilities', 'Java threads, synchronization, ExecutorService, Callable, Future, and concurrent data structures.', 0.94, 'CRITICAL', 2)
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name;

-- 4. Insert Subtopics
INSERT INTO subtopics (id, topic_id, name, content, order_index)
VALUES 
-- OS Subtopics
(1, 1, 'Critical Section Problem', 'A section of code requiring mutual exclusion to prevent race conditions among concurrent threads.', 1),
(2, 1, 'Semaphores & Mutexes', 'Counting semaphores and binary mutex locks providing atomic wait() and signal() operations.', 2),
(3, 2, 'Coffman Conditions', 'Mutual Exclusion, Hold and Wait, No Preemption, and Circular Wait must simultaneously hold for a deadlock to exist.', 1),
(4, 2, 'Bankers Algorithm (Avoidance)', 'A safety algorithm ensuring that resource allocation never transitions the system into an unsafe state.', 2),
-- Java Subtopics
(5, 6, 'List vs Set vs Map', 'List preserves insertion order and allows duplicates; Set enforces uniqueness; Map stores key-value associations.', 1),
(6, 7, 'ExecutorService & ThreadPool', 'Manages a pool of reusable worker threads, decoupling task submission from execution mechanics.', 1)
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name;

-- 5. Insert Summaries
INSERT INTO summaries (id, document_id, topic_id, summary_type, summary_text)
VALUES 
(1, 1, 1, 'TOPIC', 'Process synchronization coordinates the simultaneous execution of processes to ensure data consistency. The critical section problem is resolved by enforcing Mutual Exclusion, Progress, and Bounded Waiting using hardware primitives or software semaphores.'),
(2, 1, 2, 'TOPIC', 'Deadlocks occur when processes are permanently blocked waiting for resources held by one another. Deadlock prevention dismantles at least one of the four Coffman conditions, while avoidance dynamically checks system safety using Banker''s algorithm.'),
(3, 3, 6, 'TOPIC', 'The Java Collections Framework standardizes data structure manipulation across Java applications. Choosing between ArrayList and LinkedList or HashMap and TreeMap depends on query vs modification time complexity.'),
(4, 3, 7, 'TOPIC', 'Java Concurrency utilizes ExecutorService for managed thread execution, Callable/Future for asynchronous result retrieval, and thread-safe collections like ConcurrentHashMap to prevent data races without global locking.')
ON CONFLICT (id) DO UPDATE SET summary_text = EXCLUDED.summary_text;

-- 6. Insert Important Points
INSERT INTO important_points (id, topic_id, subtopic_id, point_text, weight)
VALUES 
(1, 1, 1, 'Mutual Exclusion guarantees that at most one process executes within the critical section at any given time.', 1.0),
(2, 1, 1, 'Progress ensures that if no process is in the critical section, selection of the next process cannot be indefinitely postponed.', 0.9),
(3, 2, 3, 'Dismantling Circular Wait by imposing a global linear ordering on all resource acquisitions prevents deadlock completely.', 1.0),
(4, 2, 4, 'Banker''s Algorithm computes Need matrix = Max - Allocation to verify if a safe sequence of process completion exists.', 0.95),
(5, 6, 5, 'ArrayList provides O(1) random access but O(n) amortized insertions at arbitrary positions; LinkedList provides O(1) pointer updates.', 0.9),
(6, 7, 6, 'ConcurrentHashMap uses bucket-level locking or CAS operations instead of synchronizing the entire map, allowing parallel reads and writes.', 1.0)
ON CONFLICT (id) DO UPDATE SET point_text = EXCLUDED.point_text;

-- 7. Insert Definitions
INSERT INTO definitions (id, topic_id, term, definition_text)
VALUES 
(1, 1, 'Race Condition', 'A situation where multiple threads access and manipulate shared data concurrently and the outcome depends on the particular order of execution.'),
(2, 1, 'Semaphore', 'A synchronization tool consisting of an integer variable accessed solely through two standard atomic operations: wait() (P) and signal() (V).'),
(3, 2, 'Deadlock', 'A state where a set of blocked processes each holds a resource and waits to acquire a resource held by another process in the same set.'),
(4, 4, 'Functional Dependency', 'A relationship between two sets of attributes where attribute X uniquely determines attribute Y (denoted X -> Y).'),
(5, 7, 'Callable', 'A generic interface representing a task that returns a result and may throw an exception, executed by an ExecutorService.')
ON CONFLICT (id) DO UPDATE SET definition_text = EXCLUDED.definition_text;

-- 8. Insert Formulas
INSERT INTO formulas (id, topic_id, expression, explanation, variables_json)
VALUES 
(1, 2, 'Need[i][j] = Max[i][j] - Allocation[i][j]', 'Calculates the remaining resource demand for process i regarding resource j in Banker''s Algorithm.', '{"Need":"Remaining required resources","Max":"Maximum resources claimed","Allocation":"Currently allocated resources"}'),
(2, 3, 'Turnaround Time = Completion Time - Arrival Time', 'Measures the total duration elapsed from process arrival to final completion in CPU scheduling.', '{"Turnaround Time":"Total time spent in system","Completion Time":"Timestamp of completion","Arrival Time":"Timestamp of submission"}')
ON CONFLICT (id) DO UPDATE SET expression = EXCLUDED.expression;

-- 9. Insert Keywords
INSERT INTO keywords (id, topic_id, keyword, relevance_score, occurrence_count)
VALUES 
(1, 1, 'Mutex', 0.95, 12),
(2, 1, 'Semaphore', 0.98, 18),
(3, 2, 'Circular Wait', 0.92, 8),
(4, 2, 'Banker Algorithm', 0.96, 14),
(5, 6, 'ArrayList', 0.94, 20),
(6, 7, 'ExecutorService', 0.97, 16)
ON CONFLICT (id) DO UPDATE SET keyword = EXCLUDED.keyword;

-- 10. Insert Revision Records
INSERT INTO revision_records (id, user_id, topic_id, revision_count, last_reviewed, importance_weight, recency_factor, revision_score, status, notes)
VALUES 
(1, 1, 1, 3, CURRENT_TIMESTAMP - INTERVAL '2 days', 0.95, 0.85, 78.5, 'RECENTLY_REVISED', 'Need to review bounded waiting proof again.'),
(2, 1, 2, 1, CURRENT_TIMESTAMP - INTERVAL '6 days', 0.90, 0.45, 42.0, 'NEEDS_REVISION', 'Practice Banker algorithm safety state problems.'),
(3, 1, 6, 5, CURRENT_TIMESTAMP - INTERVAL '1 day', 0.96, 0.95, 92.0, 'MASTERED', 'Good grasp on map implementations and hash collisions.'),
(4, 1, 7, 2, CURRENT_TIMESTAMP - INTERVAL '3 days', 0.94, 0.70, 68.0, 'STRONG', 'Reviewed thread pools and Futures.')
ON CONFLICT (id) DO UPDATE SET revision_score = EXCLUDED.revision_score;

-- 11. Insert Sample Quiz Questions
INSERT INTO quiz_questions (id, topic_id, question_text, question_type, option_a, option_b, option_c, option_d, correct_answer, explanation)
VALUES 
(1, 2, 'Which of the following is NOT one of the four Coffman conditions for deadlock?', 'MCQ', 'Mutual Exclusion', 'Hold and Wait', 'Preemption Allowed', 'Circular Wait', 'Preemption Allowed', 'Deadlock requires NO preemption; allowing preemption prevents deadlocks.'),
(2, 2, 'Banker''s Algorithm is used for which deadlock handling strategy?', 'MCQ', 'Deadlock Detection', 'Deadlock Avoidance', 'Deadlock Prevention', 'Deadlock Recovery', 'Deadlock Avoidance', 'Banker''s algorithm tests for safety before granting resource requests to avoid entering an unsafe state.'),
(3, 1, 'A binary semaphore behaves identically to a mutex lock.', 'TRUE_FALSE', 'True', 'False', NULL, NULL, 'True', 'A binary semaphore has values 0 and 1, providing mutual exclusion identical to a mutex.'),
(4, 7, 'Which method in ExecutorService blocks and waits for a Callable to return its result?', 'MCQ', 'Future.get()', 'Thread.sleep()', 'Executor.wait()', 'Task.execute()', 'Future.get()', 'Future.get() synchronously waits if necessary for the computation to complete, and then retrieves its result.')
ON CONFLICT (id) DO UPDATE SET question_text = EXCLUDED.question_text;

-- Update sequence values
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('documents_id_seq', (SELECT MAX(id) FROM documents));
SELECT setval('topics_id_seq', (SELECT MAX(id) FROM topics));
SELECT setval('subtopics_id_seq', (SELECT MAX(id) FROM subtopics));
SELECT setval('summaries_id_seq', (SELECT MAX(id) FROM summaries));
SELECT setval('important_points_id_seq', (SELECT MAX(id) FROM important_points));
SELECT setval('definitions_id_seq', (SELECT MAX(id) FROM definitions));
SELECT setval('formulas_id_seq', (SELECT MAX(id) FROM formulas));
SELECT setval('keywords_id_seq', (SELECT MAX(id) FROM keywords));
SELECT setval('revision_records_id_seq', (SELECT MAX(id) FROM revision_records));
SELECT setval('quiz_questions_id_seq', (SELECT MAX(id) FROM quiz_questions));
