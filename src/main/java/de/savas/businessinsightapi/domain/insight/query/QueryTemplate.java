package de.savas.businessinsightapi.domain.insight.query;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "query_template",
        uniqueConstraints = @UniqueConstraint(name = "uk_query_template_org_name", columnNames = {"organization_id", "name"})
)
public class QueryTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="organization_id", nullable = false)
    private Long organizationId;

    @Column(name="data_source_id", nullable = false)
    private Long dataSourceId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name="sql_text", nullable = false, columnDefinition = "text")
    private String sqlText;

    /**
     * parameters example:
     * [
     *  {"name":"dateFrom","type":"date","required":true},
     *  {"name":"dateTo","type":"date","required":true}
     * ]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private JsonNode parameters;

    /**
     * output schema example:
     * [
     *  {"name":"date","type":"date"},
     *  {"name":"amount","type":"decimal"},
     *  {"name":"currency","type":"string"}
     * ]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name="output_schema", nullable = false, columnDefinition = "jsonb")
    private JsonNode outputSchema;

    @Column(name="is_active", nullable = false)
    private boolean active = true;

    @Column(name="created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name="updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    protected QueryTemplate() {}

    public QueryTemplate(Long organizationId, Long dataSourceId, String name, String description,
                         String sqlText, JsonNode parameters, JsonNode outputSchema) {
        this.organizationId = organizationId;
        this.dataSourceId = dataSourceId;
        this.name = name;
        this.description = description;
        this.sqlText = sqlText;
        this.parameters = parameters;
        this.outputSchema = outputSchema;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public Long getOrganizationId() { return organizationId; }
    public Long getDataSourceId() { return dataSourceId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getSqlText() { return sqlText; }
    public JsonNode getParameters() { return parameters; }
    public JsonNode getOutputSchema() { return outputSchema; }
    public boolean isActive() { return active; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setSqlText(String sqlText) { this.sqlText = sqlText; }
    public void setParameters(JsonNode parameters) { this.parameters = parameters; }
    public void setOutputSchema(JsonNode outputSchema) { this.outputSchema = outputSchema; }
    public void setActive(boolean active) { this.active = active; }
}
