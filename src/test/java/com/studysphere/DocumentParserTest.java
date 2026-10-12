package com.studysphere;

import com.studysphere.document.DocumentParser;
import com.studysphere.document.DocumentParserFactory;
import com.studysphere.document.TextDocumentParser;
import com.studysphere.exception.UnsupportedFileException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Document Parser Tests (CSE1007 Polymorphism & File I/O)")
public class DocumentParserTest {

    @Test
    @DisplayName("TextDocumentParser successfully extracts text from stream")
    void testTextDocumentParserExtraction() throws Exception {
        String sampleContent = "Operating Systems\nProcess management is fundamental.\nDeadlocks must be prevented.";
        InputStream stream = new ByteArrayInputStream(sampleContent.getBytes(StandardCharsets.UTF_8));

        DocumentParser parser = new TextDocumentParser();
        String extracted = parser.extractText(stream);

        assertNotNull(extracted);
        assertTrue(extracted.contains("Operating Systems"));
        assertTrue(extracted.contains("Deadlocks must be prevented."));
    }

    @Test
    @DisplayName("DocumentParserFactory returns correct implementation based on extension")
    void testParserFactoryResolution() throws Exception {
        DocumentParser txtParser = DocumentParserFactory.getParser("sample.txt");
        assertNotNull(txtParser);
        assertEquals("TextDocumentParser", txtParser.getClass().getSimpleName());

        DocumentParser pdfParser = DocumentParserFactory.getParser("notes.pdf");
        assertNotNull(pdfParser);
        assertEquals("PDFDocumentParser", pdfParser.getClass().getSimpleName());

        DocumentParser docxParser = DocumentParserFactory.getParser("assignment.docx");
        assertNotNull(docxParser);
        assertEquals("DOCXDocumentParser", docxParser.getClass().getSimpleName());
    }

    @Test
    @DisplayName("DocumentParserFactory throws UnsupportedFileException for unsupported extensions")
    void testUnsupportedExtensionThrowsException() {
        assertThrows(UnsupportedFileException.class, () -> {
            DocumentParserFactory.getParser("archive.zip");
        });
    }
}
