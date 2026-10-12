package com.studysphere.ai;

import com.studysphere.exception.AIServiceException;
import com.studysphere.model.ImportantPoint;

import java.util.List;

/**
 * Interface defining key takeaway / important points extraction.
 */
public interface ImportantPointExtractor {
    List<ImportantPoint> extractImportantPoints(String content) throws AIServiceException;
}
