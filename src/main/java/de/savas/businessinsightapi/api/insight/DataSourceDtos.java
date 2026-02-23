package de.savas.businessinsightapi.api.insight;

import com.fasterxml.jackson.databind.JsonNode;
import de.savas.businessinsightapi.domain.insight.datasource.DataSource;
import de.savas.businessinsightapi.domain.insight.datasource.DataSourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DataSourceDtos {

    public record CreateDataSourceRequest(
            @NotBlank String name,
            @NotNull DataSourceType type,
            @NotNull JsonNode connection
    ) {}

    public record UpdateDataSourceRequest(
            String name,
            DataSourceType type,
            JsonNode connection,
            Boolean active
    ) {}

    public record DataSourceResponse(
            Long id,
            String name,
            DataSourceType type,
            JsonNode connection,
            boolean active
    ) {
        public static DataSourceResponse from(DataSource ds) {
            return new DataSourceResponse(ds.getId(), ds.getName(), ds.getType(), ds.getConnection(), ds.isActive());
        }
    }
}
