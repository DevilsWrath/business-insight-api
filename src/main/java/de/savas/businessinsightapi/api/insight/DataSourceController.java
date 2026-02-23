package de.savas.businessinsightapi.api.insight;

import de.savas.businessinsightapi.application.insight.DataSourceService;
import de.savas.businessinsightapi.api.insight.DataSourceDtos.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/insight/data-sources")
public class DataSourceController {

    private final DataSourceService service;

    public DataSourceController(DataSourceService service) {
        this.service = service;
    }

    // MVP: orgId as query param (later from JWT claims/tenant)
    @GetMapping
    public Page<DataSourceResponse> list(@RequestParam Long orgId, Pageable pageable) {
        return service.list(orgId, pageable).map(DataSourceResponse::from);
    }

    @GetMapping("/{id}")
    public DataSourceResponse get(@RequestParam Long orgId, @PathVariable Long id) {
        return DataSourceResponse.from(service.get(orgId, id));
    }

    @PostMapping
    public DataSourceResponse create(@RequestParam Long orgId, @Valid @RequestBody CreateDataSourceRequest req) {
        return DataSourceResponse.from(service.create(orgId, req.name(), req.type(), req.connection()));
    }

    @PutMapping("/{id}")
    public DataSourceResponse update(@RequestParam Long orgId, @PathVariable Long id, @Valid @RequestBody UpdateDataSourceRequest req) {
        return DataSourceResponse.from(service.update(orgId, id, req.name(), req.type(), req.connection(), req.active()));
    }
}
