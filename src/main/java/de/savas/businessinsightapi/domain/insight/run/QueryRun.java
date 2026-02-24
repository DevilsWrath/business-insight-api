package de.savas.businessinsightapi.domain.insight.run;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "query_run")
public class QueryRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="organization_id", nullable = false)
    private Long organizationId;

    @Column(name="query_template_id", nullable = false)
    private Long queryTemplateId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private QueryRunStatus status;

    @Column(name="requested_by", length = 120)
    private String requestedBy;

    @Column(name="started_at", nullable = false)
    private OffsetDateTime startedAt = OffsetDateTime.now();

    @Column(name="finished_at")
    private OffsetDateTime finishedAt;

    @Column(name="duration_ms")
    private Long durationMs;

    @Column(name="row_count")
    private Integer rowCount;

    @Column(name="error_message", columnDefinition = "text")
    private String errorMessage;

    protected QueryRun() {}

    public QueryRun(Long organizationId, Long queryTemplateId, String requestedBy) {
        this.organizationId = organizationId;
        this.queryTemplateId = queryTemplateId;
        this.requestedBy = requestedBy;
        this.status = QueryRunStatus.FAILED; // default, set to SUCCESS when done
    }

    public Long getId() { return id; }
    public Long getOrganizationId() { return organizationId; }
    public Long getQueryTemplateId() { return queryTemplateId; }
    public QueryRunStatus getStatus() { return status; }
    public String getRequestedBy() { return requestedBy; }
    public OffsetDateTime getStartedAt() { return startedAt; }
    public OffsetDateTime getFinishedAt() { return finishedAt; }
    public Long getDurationMs() { return durationMs; }
    public Integer getRowCount() { return rowCount; }
    public String getErrorMessage() { return errorMessage; }

    public void markSuccess(int rowCount, long durationMs) {
        this.status = QueryRunStatus.SUCCESS;
        this.finishedAt = OffsetDateTime.now();
        this.rowCount = rowCount;
        this.durationMs = durationMs;
        this.errorMessage = null;
    }

    public void markFailed(String message, long durationMs) {
        this.status = QueryRunStatus.FAILED;
        this.finishedAt = OffsetDateTime.now();
        this.durationMs = durationMs;
        this.errorMessage = message;
    }
}
