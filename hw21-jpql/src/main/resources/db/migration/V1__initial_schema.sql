-- Для @GeneratedValue(strategy = GenerationType.IDENTITY)
/*
create table client
(
    id   bigserial not null primary key,
    name varchar(50)
);

 */

-- Для @GeneratedValue(strategy = GenerationType.SEQUENCE)
create sequence hibernate_sequence start with 1 increment by 1;

create table client
(
    id   bigint not null primary key,
    name varchar(50)
    -- address_id varchar(150)
);

--create table address
--(
--    id   bigint not null primary key,
--    street varchar(150)
--);

--create table phones
--(
--    id   bigint not null primary key,
--    number varchar(150),
--    client_id bigint
--);