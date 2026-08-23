-- Optional manual script if you prefer to create DB/user before first app start.
-- Flyway migrations in the app will create schema and tables.

-- Run as postgres superuser:
-- CREATE DATABASE tms;
-- CREATE USER tms WITH PASSWORD 'tms';
-- GRANT ALL PRIVILEGES ON DATABASE tms TO tms;

-- After first Flyway migration (as superuser on database tms):
-- GRANT ALL ON SCHEMA tms TO tms;
-- GRANT ALL ON ALL TABLES IN SCHEMA tms TO tms;
-- GRANT ALL ON ALL SEQUENCES IN SCHEMA tms TO tms;
-- ALTER DEFAULT PRIVILEGES IN SCHEMA tms GRANT ALL ON TABLES TO tms;
-- ALTER DEFAULT PRIVILEGES IN SCHEMA tms GRANT ALL ON SEQUENCES TO tms;
