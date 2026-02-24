package de.savas.businessinsightapi.api.insight;

import de.savas.businessinsightapi.api.insight.QueryTemplateDtos.*;
import de.savas.businessinsightapi.application.insight.QueryTemplateService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/insight/query-templates")
public class QueryTemplateController {

    private final QueryTemplateService service;

    public QueryTemplateController(QueryTemplateService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('INSIGHT_QT_READ')")
    public Page<QueryTemplateResponse> list(@RequestParam Long orgId, Pageable pageable) {
        return service.list(orgId, pageable).map(QueryTemplateResponse::from);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INSIGHT_QT_READ')")
    public QueryTemplateResponse get(@RequestParam Long orgId, @PathVariable Long id) {
        return QueryTemplateResponse.from(service.get(orgId, id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INSIGHT_QT_CREATE')")
    public QueryTemplateResponse create(@RequestParam Long orgId, @Valid @RequestBody CreateQueryTemplateRequest req) {
        return QueryTemplateResponse.from(service.create(
                orgId,
                req.dataSourceId(),
                req.name(),
                req.description(),
                req.sqlText(),
                req.parameters(),
                req.outputSchema()
        ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('INSIGHT_QT_UPDATE')")
    public QueryTemplateResponse update(@RequestParam Long orgId, @PathVariable Long id, @Valid @RequestBody UpdateQueryTemplateRequest req) {
        return QueryTemplateResponse.from(service.update(
                orgId,
                id,
                req.name(),
                req.description(),
                req.sqlText(),
                req.parameters(),
                req.outputSchema(),
                req.active()
        ));
    }
}
