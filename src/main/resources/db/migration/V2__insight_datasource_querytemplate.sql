create table if not exists organization (
                                            id bigserial primary key,
                                            name varchar(200) not null,
    created_at timestamptz not null default now()
    );

insert into organization(name)
select 'Default Org'
    where not exists (select 1 from organization);

create table if not exists data_source (
                                           id bigserial primary key,
                                           organization_id bigint not null references organization(id),
    name varchar(200) not null,
    type varchar(50) not null,
    connection jsonb not null,
    is_active boolean not null default true,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique (organization_id, name)
    );

create table if not exists query_template (
                                              id bigserial primary key,
                                              organization_id bigint not null references organization(id),
    data_source_id bigint not null references data_source(id),
    name varchar(200) not null,
    description varchar(500),
    sql_text text not null,
    parameters jsonb not null default '[]'::jsonb,
    output_schema jsonb not null default '[]'::jsonb,
    is_active boolean not null default true,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique (organization_id, name)
    );

create index if not exists ix_data_source_org on data_source(organization_id);
create index if not exists ix_query_template_org on query_template(organization_id);
create index if not exists ix_query_template_ds on query_template(data_source_id);