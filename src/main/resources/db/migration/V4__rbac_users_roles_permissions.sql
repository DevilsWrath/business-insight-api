alter table app_user add column if not exists username varchar(120);

update app_user
set username = email
where (username is null or trim(username) = '');

create unique index if not exists ux_app_user_username on app_user(username);

create table if not exists app_role (
                                        id bigserial primary key,
                                        name varchar(80) not null unique,
                                        description varchar(255)
);

create table if not exists app_permission (
                                              id bigserial primary key,
                                              code varchar(120) not null unique,
                                              description varchar(255)
);

create table if not exists app_user_role (
                                             user_id bigint not null references app_user(id) on delete cascade,
                                             role_id bigint not null references app_role(id) on delete cascade,
                                             primary key (user_id, role_id)
);

create table if not exists app_role_permission (
                                                   role_id bigint not null references app_role(id) on delete cascade,
                                                   permission_id bigint not null references app_permission(id) on delete cascade,
                                                   primary key (role_id, permission_id)
);