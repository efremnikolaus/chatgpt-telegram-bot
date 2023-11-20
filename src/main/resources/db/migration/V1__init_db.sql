CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       password VARCHAR(100),
                       username VARCHAR(50) UNIQUE
);