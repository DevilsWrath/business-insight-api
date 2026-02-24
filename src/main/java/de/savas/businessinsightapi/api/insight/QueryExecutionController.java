package de.savas.businessinsightapi.api.insight;

import de.savas.businessinsightapi.application.insight.QueryExecutionService;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/insight/query-execution")
public class QueryExecutionController {

    private final QueryExecutionService service;

    public QueryExecutionController(QueryExecutionService service) {
        this.service = service;
    }

    public record ExecuteRequest(
            @NotNull Long queryTemplateId,
            Map<String, Object> params,
            Boolean snapshot
    ) {}

    public record ExecuteResponse(
            Long queryRunId,
            int rowCount,
            long durationMs,
            Long snapshotId
    ) {}

    @PostMapping("/execute")
    @PreAuthorize("hasAuthority('INSIGHT_QUERY_EXECUTE')")
    public ExecuteResponse execute(@RequestParam Long orgId, @RequestBody ExecuteRequest req) {
        boolean takeSnapshot = req.snapshot() != null && req.snapshot();
        var result = service.execute(orgId, req.queryTemplateId(), req.params(), takeSnapshot);
        return new ExecuteResponse(result.queryRunId(), result.rowCount(), result.durationMs(), result.snapshotId());
    }
}
