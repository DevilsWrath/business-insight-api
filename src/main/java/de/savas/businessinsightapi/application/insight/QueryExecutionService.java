package de.savas.businessinsightapi.application.insight;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.savas.businessinsightapi.common.error.BadRequestException;
import de.savas.businessinsightapi.domain.insight.query.QueryTemplate;
import de.savas.businessinsightapi.domain.insight.query.QueryTemplateRepository;
import de.savas.businessinsightapi.domain.insight.run.QueryRun;
import de.savas.businessinsightapi.domain.insight.run.QueryRunRepository;
import de.savas.businessinsightapi.domain.insight.snapshot.DatasetSnapshot;
import de.savas.businessinsightapi.domain.insight.snapshot.DatasetSnapshotRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class QueryExecutionService {

    private static final int MAX_ROWS = 5000;

    private final QueryTemplateRepository queryTemplateRepo;
    private final QueryRunRepository queryRunRepo;
    private final DatasetSnapshotRepository snapshotRepo;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public QueryExecutionService(
            QueryTemplateRepository queryTemplateRepo,
            QueryRunRepository queryRunRepo,
            DatasetSnapshotRepository snapshotRepo,
            JdbcTemplate jdbcTemplate,
            ObjectMapper objectMapper
    ) {
        this.queryTemplateRepo = queryTemplateRepo;
        this.queryRunRepo = queryRunRepo;
        this.snapshotRepo = snapshotRepo;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public ExecutionResult execute(Long orgId, Long queryTemplateId, Map<String, Object> params, boolean snapshot) {
        QueryTemplate qt = queryTemplateRepo.findById(queryTemplateId)
                .orElseThrow(() -> new BadRequestException("QueryTemplate not found"));

        if (!qt.getOrganizationId().equals(orgId)) {
            throw new BadRequestException("QueryTemplate not found");
        }

        enforceSelectOnly(qt.getSqlText());

        QueryRun run = queryRunRepo.save(new QueryRun(orgId, queryTemplateId, "system")); // later: from JWT

        Instant start = Instant.now();
        try {
            // MVP: no named param binding yet. We run raw SELECT and force a LIMIT.
            // Next sprint: NamedParameterJdbcTemplate + parameter mapping/whitelisting.
            String sql = applyLimitIfMissing(qt.getSqlText(), MAX_ROWS);

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

            long durationMs = Duration.between(start, Instant.now()).toMillis();
            run.markSuccess(rows.size(), durationMs);
            queryRunRepo.save(run);

            DatasetSnapshot savedSnapshot = null;
            if (snapshot) {
                JsonNode json = toJson(rows);
                savedSnapshot = snapshotRepo.save(new DatasetSnapshot(orgId, run.getId(), json));
            }

            return new ExecutionResult(run.getId(), rows.size(), durationMs, savedSnapshot == null ? null : savedSnapshot.getId());
        } catch (Exception ex) {
            long durationMs = Duration.between(start, Instant.now()).toMillis();
            run.markFailed(safeMessage(ex), durationMs);
            queryRunRepo.save(run);
            throw new BadRequestException("Query execution failed: " + safeMessage(ex));
        }
    }

    private void enforceSelectOnly(String sqlText) {
        String normalized = sqlText == null ? "" : sqlText.trim().toLowerCase();
        if (!normalized.startsWith("select")) {
            throw new BadRequestException("Only SELECT queries are allowed");
        }
        // quick deny-list
        if (normalized.contains(";") || normalized.contains("drop ") || normalized.contains("delete ")
                || normalized.contains("update ") || normalized.contains("insert ") || normalized.contains("alter ")) {
            throw new BadRequestException("Only a single SELECT statement is allowed");
        }
    }

    private String applyLimitIfMissing(String sql, int maxRows) {
        String normalized = sql.trim().toLowerCase();
        if (normalized.contains(" limit ")) return sql;
        return sql + " limit " + maxRows;
    }

    private JsonNode toJson(List<Map<String, Object>> rows) {
        ArrayNode arr = objectMapper.createArrayNode();
        for (Map<String, Object> row : rows) {
            ObjectNode obj = objectMapper.createObjectNode();
            row.forEach((k, v) -> obj.set(k, objectMapper.valueToTree(v)));
            arr.add(obj);
        }
        return arr;
    }

    private String safeMessage(Exception ex) {
        String msg = ex.getMessage();
        if (msg == null) return "unknown_error";
        // Avoid leaking credentials
        return msg.length() > 600 ? msg.substring(0, 600) : msg;
    }

    public record ExecutionResult(Long queryRunId, int rowCount, long durationMs, Long snapshotId) {}
}