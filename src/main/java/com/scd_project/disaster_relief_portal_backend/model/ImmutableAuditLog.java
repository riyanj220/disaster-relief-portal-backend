package com.scd_project.disaster_relief_portal_backend.model;

import lombok.Getter;

/**
 * Once created, this log entry cannot be modified.
 */
@Getter
public final class ImmutableAuditLog {
    private final String logId;
    private final String action;
    private final long timestamp;
    private final String performedBy;

    public ImmutableAuditLog(String logId, String action, String performedBy) {
        this.logId = logId;
        this.action = action;
        this.performedBy = performedBy;
        this.timestamp = System.currentTimeMillis();
    }
}