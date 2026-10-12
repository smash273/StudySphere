package com.studysphere.document;

import com.studysphere.exception.DocumentProcessingException;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Concrete parser for Microsoft Word (.docx) documents using Apache POI.
 */
public class DOCXDocumentParser implements DocumentParser {
    private static final Logger logger = LoggerFactory.getLogger(DOCXDocumentParser.class);

    @Override
    public String extractText(File file) throws DocumentProcessingException {
        if (file == null || !file.exists() || !file.canRead()) {
            throw new DocumentProcessingException("DOCX file cannot be read or does not exist.");
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            return extractText(fis);
        } catch (DocumentProcessingException e) {
            throw e;
        } catch (Exception e) {
            logger.error("DOCX extraction error: {}", e.getMessage(), e);
            throw new DocumentProcessingException("Failed to read DOCX file: " + e.getMessage(), e);
        }
    }

    @Override
    public String extractText(InputStream inputStream) throws DocumentProcessingException {
        if (inputStream == null) {
            throw new DocumentProcessingException("InputStream is null.");
        }
        try (XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            String text = extractor.getText();
            if (text == null || text.trim().isEmpty()) {
                throw new DocumentProcessingException("No readable academic text was found in this DOCX document.");
            }
            return cleanExtractedText(text);
        } catch (DocumentProcessingException e) {
            throw e;
        } catch (Exception e) {
            logger.error("POI DOCX extraction error: {}", e.getMessage(), e);
            throw new DocumentProcessingException("Failed to parse DOCX: " + e.getMessage(), e);
        }
    }

    @Override
    public String getSupportedExtension() {
        return "DOCX";
    }

    private String cleanExtractedText(String text) {
        return text.replace("\r\n", "\n")
                   .replaceAll("[ \\t]+", " ")
                   .replaceAll("\\n{3,}", "\n\n")
                   .trim();
    }
}
