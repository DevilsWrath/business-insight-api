package de.savas.businessinsightapi.api.insight;

import com.fasterxml.jackson.databind.JsonNode;
import de.savas.businessinsightapi.domain.insight.query.QueryTemplate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class QueryTemplateDtos {

    public record CreateQueryTemplateRequest(
            @NotNull Long dataSourceId,
            @NotBlank String name,
            String description,
            @NotBlank String sqlText,
            JsonNode parameters,
            JsonNode outputSchema
    ) {}

    public record UpdateQueryTemplateRequest(
            String name,
            String description,
            String sqlText,
            JsonNode parameters,
            JsonNode outputSchema,
            Boolean active
    ) {}

    public record QueryTemplateResponse(
            Long id,
            Long dataSourceId,
            String name,
            String description,
            String sqlText,
            JsonNode parameters,
            JsonNode outputSchema,
            boolean active
    ) {
        public static QueryTemplateResponse from(QueryTemplate qt) {
            return new QueryTemplateResponse(
                    qt.getId(),
                    qt.getDataSourceId(),
                    qt.getName(),
                    qt.getDescription(),
                    qt.getSqlText(),
                    qt.getParameters(),
                    qt.getOutputSchema(),
                    qt.isActive()
            );
        }
    }
}
