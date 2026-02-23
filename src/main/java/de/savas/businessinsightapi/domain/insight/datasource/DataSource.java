package de.savas.businessinsightapi.domain.insight.datasource;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "data_source",
        uniqueConstraints = @UniqueConstraint(name = "uk_data_source_org_name", columnNames = {"organization_id", "name"})
)
public class DataSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="organization_id", nullable = false)
    private Long organizationId;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DataSourceType type;

    /**
     * Example JSON:
     * {
     *   "host": "10.0.0.10",
     *   "port": 1433,
     *   "database": "ERP",
     *   "username": "readonly_user",
     *   "secretRef": "env:ERP_MSSQL_PASSWORD"
     * }
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private JsonNode connection;

    @Column(name="is_active", nullable = false)
    private boolean active = true;

    @Column(name="created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name="updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    protected DataSource() {}

    public DataSource(Long organizationId, String name, DataSourceType type, JsonNode connection) {
        this.organizationId = organizationId;
        this.name = name;
        this.type = type;
        this.connection = connection;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public Long getOrganizationId() { return organizationId; }
    public String getName() { return name; }
    public DataSourceType getType() { return type; }
    public JsonNode getConnection() { return connection; }
    public boolean isActive() { return active; }

    public void setName(String name) { this.name = name; }
    public void setType(DataSourceType type) { this.type = type; }
    public void setConnection(JsonNode connection) { this.connection = connection; }
    public void setActive(boolean active) { this.active = active; }
}
