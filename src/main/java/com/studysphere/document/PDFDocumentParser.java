package com.studysphere.document;

import com.studysphere.exception.DocumentProcessingException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

/**
 * Concrete parser for PDF documents using Apache PDFBox 3.
 * Demonstrates Polymorphism and Third-Party API integration.
 */
public class PDFDocumentParser implements DocumentParser {
    private static final Logger logger = LoggerFactory.getLogger(PDFDocumentParser.class);

    @Override
    public String extractText(File file) throws DocumentProcessingException {
        if (file == null || !file.exists() || !file.canRead()) {
            throw new DocumentProcessingException("PDF file cannot be read or does not exist.");
        }
        try (PDDocument document = Loader.loadPDF(file)) {
            if (document.isEncrypted()) {
                throw new DocumentProcessingException("Encrypted PDF documents are not supported without a password.");
            }
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(document);
            if (text == null || text.trim().isEmpty()) {
                throw new DocumentProcessingException("No readable academic text was found in this PDF document.");
            }
            return cleanExtractedText(text);
        } catch (DocumentProcessingException e) {
            throw e;
        } catch (Exception e) {
            logger.error("PDFBox extraction error: {}", e.getMessage(), e);
            throw new DocumentProcessingException("Failed to extract text from PDF: " + e.getMessage(), e);
        }
    }

    @Override
    public String extractText(InputStream inputStream) throws DocumentProcessingException {
        if (inputStream == null) {
            throw new DocumentProcessingException("InputStream is null.");
        }
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[8192];
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] bytes = buffer.toByteArray();

            try (PDDocument document = Loader.loadPDF(bytes)) {
                if (document.isEncrypted()) {
                    throw new DocumentProcessingException("Encrypted PDF documents are not supported without a password.");
                }
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                String text = stripper.getText(document);
                if (text == null || text.trim().isEmpty()) {
                    throw new DocumentProcessingException("No readable academic text was found in this PDF document.");
                }
                return cleanExtractedText(text);
            }
        } catch (DocumentProcessingException e) {
            throw e;
        } catch (Exception e) {
            logger.error("PDF stream extraction error: {}", e.getMessage(), e);
            throw new DocumentProcessingException("Failed to parse PDF stream: " + e.getMessage(), e);
        }
    }

    @Override
    public String getSupportedExtension() {
        return "PDF";
    }

    private String cleanExtractedText(String text) {
        return text.replace("\r\n", "\n")
                   .replaceAll("[ \\t]+", " ")
                   .replaceAll("\\n{3,}", "\n\n")
                   .trim();
    }
}
