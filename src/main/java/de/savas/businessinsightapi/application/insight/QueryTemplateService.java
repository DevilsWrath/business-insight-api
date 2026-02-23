package de.savas.businessinsightapi.application.insight;

import com.fasterxml.jackson.databind.JsonNode;
import de.savas.businessinsightapi.common.error.BadRequestException;
import de.savas.businessinsightapi.domain.insight.datasource.DataSourceRepository;
import de.savas.businessinsightapi.domain.insight.query.QueryTemplate;
import de.savas.businessinsightapi.domain.insight.query.QueryTemplateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class QueryTemplateService {

    private final QueryTemplateRepository repo;
    private final DataSourceRepository dataSourceRepo;

    public QueryTemplateService(QueryTemplateRepository repo, DataSourceRepository dataSourceRepo) {
        this.repo = repo;
        this.dataSourceRepo = dataSourceRepo;
    }

    public Page<QueryTemplate> list(Long organizationId, Pageable pageable) {
        return repo.findAllByOrganizationId(organizationId, pageable);
    }

    public QueryTemplate get(Long organizationId, Long id) {
        QueryTemplate qt = repo.findById(id).orElseThrow(() -> new BadRequestException("QueryTemplate not found"));
        if (!qt.getOrganizationId().equals(organizationId)) {
            throw new BadRequestException("QueryTemplate not found");
        }
        return qt;
    }

    public QueryTemplate create(Long organizationId,
                                Long dataSourceId,
                                String name,
                                String description,
                                String sqlText,
                                JsonNode parameters,
                                JsonNode outputSchema) {

        // ensure datasource exists & belongs to org
        var ds = dataSourceRepo.findById(dataSourceId).orElseThrow(() -> new BadRequestException("DataSource not found"));
        if (!ds.getOrganizationId().equals(organizationId)) {
            throw new BadRequestException("DataSource not found");
        }

        if (sqlText == null || sqlText.isBlank()) {
            throw new BadRequestException("sqlText is required");
        }
        // basic safety for MVP: allow only SELECT (real parser later)
        String normalized = sqlText.trim().toLowerCase();
        if (!normalized.startsWith("select")) {
            throw new BadRequestException("Only SELECT queries are allowed");
        }

        if (parameters == null) parameters = com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.arrayNode();
        if (outputSchema == null) outputSchema = com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.arrayNode();

        return repo.save(new QueryTemplate(
                organizationId,
                dataSourceId,
                name,
                description,
                sqlText,
                parameters,
                outputSchema
        ));
    }

    public QueryTemplate update(Long organizationId,
                                Long id,
                                String name,
                                String description,
                                String sqlText,
                                JsonNode parameters,
                                JsonNode outputSchema,
                                Boolean active) {

        QueryTemplate qt = get(organizationId, id);

        if (name != null) qt.setName(name);
        if (description != null) qt.setDescription(description);

        if (sqlText != null) {
            String normalized = sqlText.trim().toLowerCase();
            if (!normalized.startsWith("select")) {
                throw new BadRequestException("Only SELECT queries are allowed");
            }
            qt.setSqlText(sqlText);
        }

        if (parameters != null) qt.setParameters(parameters);
        if (outputSchema != null) qt.setOutputSchema(outputSchema);
        if (active != null) qt.setActive(active);

        return repo.save(qt);
    }
}
