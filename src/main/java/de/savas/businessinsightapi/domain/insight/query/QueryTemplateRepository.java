package de.savas.businessinsightapi.domain.insight.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueryTemplateRepository extends JpaRepository<QueryTemplate, Long> {
    Page<QueryTemplate> findAllByOrganizationId(Long organizationId, Pageable pageable);
}