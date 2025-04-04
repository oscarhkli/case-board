create table if not exists cases
(
    id                     bigint not null auto_increment,
    case_number            varchar(255) unique not null,
    title                  varchar(255)        not null,
    description            text,
    status                 varchar(255)        not null,
    created_datetime       timestamp,
    last_modified_datetime timestamp,
    primary key (id),
    index idx_cases_id (id)
);
