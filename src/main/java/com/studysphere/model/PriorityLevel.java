package com.studysphere.model;

/**
 * Priority levels for academic topics based on importance and syllabus weight.
 */
public enum PriorityLevel {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    CRITICAL(4);

    private final int weight;

    PriorityLevel(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public static PriorityLevel fromScore(double score) {
        if (score >= 0.90) return CRITICAL;
        if (score >= 0.75) return HIGH;
        if (score >= 0.50) return MEDIUM;
        return LOW;
    }
}
