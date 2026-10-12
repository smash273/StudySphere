package com.studysphere.document;

import com.studysphere.exception.DocumentProcessingException;

import java.io.File;
import java.io.InputStream;

/**
 * Interface defining the contract for document parsers.
 * Demonstrates Abstraction and Interface-based design in Java.
 */
public interface DocumentParser {

    /**
     * Extracts readable academic text from an InputStream.
     * @param inputStream the input stream of the document
     * @return cleaned text content
     * @throws DocumentProcessingException if parsing fails
     */
    String extractText(InputStream inputStream) throws DocumentProcessingException;

    /**
     * Extracts readable academic text from a File.
     * @param file the document file
     * @return cleaned text content
     * @throws DocumentProcessingException if parsing fails
     */
    String extractText(File file) throws DocumentProcessingException;

    /**
     * Returns the file extension supported by this parser (e.g. "PDF", "DOCX", "TXT").
     */
    String getSupportedExtension();
}
