package com.studysphere.document;

import com.studysphere.exception.UnsupportedFileException;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for instantiating and resolving appropriate DocumentParser instances.
 * Demonstrates Factory Pattern and Collections Framework (Map).
 */
public class DocumentParserFactory {
    private static final Map<String, DocumentParser> parsers = new HashMap<>();

    static {
        registerParser("pdf", new PDFDocumentParser());
        registerParser("docx", new DOCXDocumentParser());
        registerParser("txt", new TextDocumentParser());
    }

    public static void registerParser(String extension, DocumentParser parser) {
        parsers.put(extension.toLowerCase(), parser);
    }

    public static DocumentParser getParser(String filename) throws UnsupportedFileException {
        if (filename == null || !filename.contains(".")) {
            throw new UnsupportedFileException("unknown");
        }
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        DocumentParser parser = parsers.get(extension);
        if (parser == null) {
            throw new UnsupportedFileException(extension);
        }
        return parser;
    }

    public static boolean isSupported(String filename) {
        if (filename == null || !filename.contains(".")) return false;
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return parsers.containsKey(extension);
    }
}
