package de.savas.businessinsightapi.application.insight;

import de.savas.businessinsightapi.common.error.BadRequestException;
import de.savas.businessinsightapi.domain.insight.datasource.DataSource;
import de.savas.businessinsightapi.domain.insight.datasource.DataSourceRepository;
import de.savas.businessinsightapi.domain.insight.datasource.DataSourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class DataSourceService {

    private final DataSourceRepository repo;

    public DataSourceService(DataSourceRepository repo) {
        this.repo = repo;
    }

    public Page<DataSource> list(Long organizationId, Pageable pageable) {
        return repo.findAllByOrganizationId(organizationId, pageable);
    }

    public DataSource get(Long organizationId, Long id) {
        DataSource ds = repo.findById(id).orElseThrow(() -> new BadRequestException("DataSource not found"));
        if (!ds.getOrganizationId().equals(organizationId)) {
            throw new BadRequestException("DataSource not found");
        }
        return ds;
    }

    public DataSource create(Long organizationId, String name, DataSourceType type, JsonNode connection) {
        if (connection == null || connection.isNull()) {
            throw new BadRequestException("connection is required");
        }
        return repo.save(new DataSource(organizationId, name, type, connection));
    }

    public DataSource update(Long organizationId, Long id, String name, DataSourceType type, JsonNode connection, Boolean active) {
        DataSource ds = get(organizationId, id);
        if (name != null) ds.setName(name);
        if (type != null) ds.setType(type);
        if (connection != null && !connection.isNull()) ds.setConnection(connection);
        if (active != null) ds.setActive(active);
        return repo.save(ds);
    }
}
