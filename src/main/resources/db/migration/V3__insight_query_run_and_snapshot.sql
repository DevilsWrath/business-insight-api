create table if not exists query_run (
                                         id bigserial primary key,
                                         organization_id bigint not null references organization(id),
    query_template_id bigint not null references query_template(id),

    status varchar(30) not null, -- SUCCESS, FAILED
    requested_by varchar(120),   -- MVP: username/username later from JWT

    started_at timestamptz not null default now(),
    finished_at timestamptz,
    duration_ms bigint,
    row_count int,

    error_message text
    );

create index if not exists ix_query_run_org on query_run(organization_id);
create index if not exists ix_query_run_qt on query_run(query_template_id);

-- Snapshot is optional for now, but we lay the table for the next sprint
create table if not exists dataset_snapshot (
                                                id bigserial primary key,
                                                organization_id bigint not null references organization(id),
    query_run_id bigint not null references query_run(id),

    storage_format varchar(30) not null default 'JSON',
    -- MVP: store small results as jsonb. Later: S3/minio/parquet
    data jsonb,

    created_at timestamptz not null default now()
    );

create index if not exists ix_snapshot_org on dataset_snapshot(organization_id);
create index if not exists ix_snapshot_run on dataset_snapshot(query_run_id);