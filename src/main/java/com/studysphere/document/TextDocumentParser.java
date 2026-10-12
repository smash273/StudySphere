package com.studysphere.document;

import com.studysphere.exception.DocumentProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;

/**
 * Concrete parser for Plain Text (.txt) files using standard Java NIO.
 */
public class TextDocumentParser implements DocumentParser {
    private static final Logger logger = LoggerFactory.getLogger(TextDocumentParser.class);

    @Override
    public String extractText(File file) throws DocumentProcessingException {
        if (file == null || !file.exists() || !file.canRead()) {
            throw new DocumentProcessingException("Text file cannot be read or does not exist.");
        }
        try {
            String text = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            if (text == null || text.trim().isEmpty()) {
                throw new DocumentProcessingException("No readable academic text was found in this document.");
            }
            return cleanExtractedText(text);
        } catch (DocumentProcessingException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Text file read error: {}", e.getMessage(), e);
            throw new DocumentProcessingException("Failed to read text file: " + e.getMessage(), e);
        }
    }

    @Override
    public String extractText(InputStream inputStream) throws DocumentProcessingException {
        if (inputStream == null) {
            throw new DocumentProcessingException("InputStream is null.");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String text = reader.lines().collect(Collectors.joining("\n"));
            if (text == null || text.trim().isEmpty()) {
                throw new DocumentProcessingException("No readable academic text was found in this document.");
            }
            return cleanExtractedText(text);
        } catch (DocumentProcessingException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Text stream read error: {}", e.getMessage(), e);
            throw new DocumentProcessingException("Failed to read text stream: " + e.getMessage(), e);
        }
    }

    @Override
    public String getSupportedExtension() {
        return "TXT";
    }

    private String cleanExtractedText(String text) {
        return text.replace("\r\n", "\n")
                   .replaceAll("[ \\t]+", " ")
                   .replaceAll("\\n{3,}", "\n\n")
                   .trim();
    }
}
