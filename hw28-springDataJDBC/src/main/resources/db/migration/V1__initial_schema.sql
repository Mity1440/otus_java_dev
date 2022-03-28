create table client
(
    id   bigserial not null primary key,
    name varchar(50) not null
);

create table addresses
(
    id bigserial not null primary key,
    street varchar(150),
    client_id bigserial not null references client(id)
);

create table phones
(
    id bigserial not null primary key,
    number varchar(150),
    client_id bigserial not null references client(id)
);