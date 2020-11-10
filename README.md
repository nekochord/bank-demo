# Bank demo


## DB configuration
```
CREATE USER user_service WITH PASSWORD 'user0218';
CREATE SCHEMA user_service;
GRANT ALL PRIVILEGES ON SCHEMA user_service TO user_service;

CREATE TABLE user_service.user(
    id bigint PRIMARY KEY,
    identity_number varchar(255) UNIQUE,
    first_name varchar(255),
    last_name varchar(255),
    password varchar(255)
);
```
