package de.savas.businessinsightapi.domain.insight.snapshot;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "dataset_snapshot")
public class DatasetSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="organization_id", nullable = false)
    private Long organizationId;

    @Column(name="query_run_id", nullable = false)
    private Long queryRunId;

    @Column(name="storage_format", nullable = false, length = 30)
    private String storageFormat = "JSON";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode data;

    @Column(name="created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    protected DatasetSnapshot() {}

    public DatasetSnapshot(Long organizationId, Long queryRunId, JsonNode data) {
        this.organizationId = organizationId;
        this.queryRunId = queryRunId;
        this.data = data;
    }

    public Long getId() { return id; }
    public Long getOrganizationId() { return organizationId; }
    public Long getQueryRunId() { return queryRunId; }
    public String getStorageFormat() { return storageFormat; }
    public JsonNode getData() { return data; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
