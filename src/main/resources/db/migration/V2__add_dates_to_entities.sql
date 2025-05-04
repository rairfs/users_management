ALTER TABLE users
    ADD COLUMN created_at TIMESTAMP DEFAULT now(),
    ADD COLUMN updated_at TIMESTAMP null;

ALTER TABLE addresses
    ADD COLUMN created_at TIMESTAMP DEFAULT now(),
    ADD COLUMN updated_at TIMESTAMP null;