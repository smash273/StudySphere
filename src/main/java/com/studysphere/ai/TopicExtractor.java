package com.studysphere.ai;

import com.studysphere.exception.AIServiceException;
import com.studysphere.model.Topic;

import java.util.List;

/**
 * Interface defining the extraction contract for academic topics.
 */
public interface TopicExtractor {
    List<Topic> extractTopics(String content) throws AIServiceException;
}
