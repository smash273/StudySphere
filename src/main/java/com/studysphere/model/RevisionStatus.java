package com.studysphere.model;

/**
 * Academic retention and mastery status for a topic.
 */
public enum RevisionStatus {
    NEEDS_REVISION("Requires immediate review", "#ef4444"),
    RECENTLY_REVISED("Reviewed recently", "#f59e0b"),
    STRONG("Good retention", "#3b82f6"),
    MASTERED("Mastered topic", "#10b981");

    private final String label;
    private final String badgeColor;

    RevisionStatus(String label, String badgeColor) {
        this.label = label;
        this.badgeColor = badgeColor;
    }

    public String getLabel() {
        return label;
    }

    public String getBadgeColor() {
        return badgeColor;
    }

    public static RevisionStatus calculateStatus(double score) {
        if (score >= 85.0) return MASTERED;
        if (score >= 65.0) return STRONG;
        if (score >= 45.0) return RECENTLY_REVISED;
        return NEEDS_REVISION;
    }
}
