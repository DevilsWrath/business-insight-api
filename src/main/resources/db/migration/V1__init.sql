create table if not exists app_user (
                                        id bigserial primary key,
                                        email varchar(255) not null unique,
    username varchar(255) not null,
    password_hash varchar(255) not null,
    created_at timestamptz not null default now()
    );