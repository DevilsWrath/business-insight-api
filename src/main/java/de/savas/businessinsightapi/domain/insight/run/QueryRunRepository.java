package de.savas.businessinsightapi.domain.insight.run;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueryRunRepository extends JpaRepository<QueryRun, Long> {
    Page<QueryRun> findAllByOrganizationId(Long organizationId, Pageable pageable);
}
