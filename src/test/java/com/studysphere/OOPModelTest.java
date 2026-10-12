package com.studysphere;

import com.studysphere.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OOP Domain Model Tests (CSE1007 Inheritance, Encapsulation, Polymorphism)")
public class OOPModelTest {

    @Test
    @DisplayName("StudyMaterial inheritance hierarchy works polymorphically")
    void testStudyMaterialInheritance() {
        StudyMaterial pdf = new PDFMaterial(1, "OS Ch1", "os.pdf", 2048L, "path/os.pdf", 10, false);
        StudyMaterial docx = new DOCXMaterial(2, "DBMS Notes", "dbms.docx", 4096L, "path/dbms.docx", 15, 2);
        StudyMaterial txt = new TextMaterial(3, "Java Syllabus", "java.txt", 1024L, "path/java.txt", 50, "UTF-8");

        assertEquals("PDF", pdf.getMaterialType());
        assertEquals("DOCX", docx.getMaterialType());
        assertEquals("TXT", txt.getMaterialType());

        // Polymorphic behavior
        StudyMaterial[] materials = { pdf, docx, txt };
        for (StudyMaterial m : materials) {
            assertNotNull(m.getTitle());
            assertTrue(m.getFileSizeBytes() > 0);
            assertTrue(m.estimateProcessingTimeSeconds() > 0);
        }
    }

    @Test
    @DisplayName("User and Student inheritance and encapsulation")
    void testStudentInheritance() {
        Student student = new Student();
        student.setId(101);
        student.setUsername("alex_doe");
        student.setEmail("alex@vit.ac.in");
        student.setFullName("Alex Doe");
        student.setStudentId("21BCE0001");
        student.setEnrollmentNo("EN20210001");
        student.setMajor("Computer Science");
        student.setSemester(5);
        student.setRole(UserRole.STUDENT);

        assertTrue(student instanceof User);
        assertEquals("Alex Doe", student.getFullName());
        assertEquals("21BCE0001", student.getStudentId());
        assertEquals(UserRole.STUDENT, student.getRole());
    }

    @Test
    @DisplayName("Topic, SubTopic, Summary, and Point compositions")
    void testTopicHierarchyComposition() {
        Topic topic = new Topic(1, "Virtual Memory", "Paging and segmentation mechanisms", 0.95, 1);
        SubTopic sub = new SubTopic(1, "Page Replacement", "FIFO, LRU and Optimal algorithms", 1);
        topic.getSubtopics().add(sub);

        Summary summary = new Summary(1, 1, "TOPIC", "Virtual memory decouples logical memory from physical memory.");
        topic.setSummary(summary);

        ImportantPoint pt = new ImportantPoint(1, null, "LRU does not suffer from Belady's Anomaly.", 1.0);
        topic.getImportantPoints().add(pt);

        assertEquals(1, topic.getSubtopics().size());
        assertEquals("Page Replacement", topic.getSubtopics().get(0).getName());
        assertNotNull(topic.getSummary());
        assertEquals(1, topic.getImportantPoints().size());
        assertEquals(PriorityLevel.CRITICAL, topic.getPriorityLevel());
    }
}
