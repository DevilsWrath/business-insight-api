package de.savas.businessinsightapi.domain.insight.datasource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataSourceRepository extends JpaRepository<DataSource, Long> {
    Page<DataSource> findAllByOrganizationId(Long organizationId, Pageable pageable);
}
