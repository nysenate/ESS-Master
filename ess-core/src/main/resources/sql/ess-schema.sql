--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: ess; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA ess;


ALTER SCHEMA ess OWNER TO postgres;

SET search_path = ess, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: user_roles; Type: TABLE; Schema: ess; Owner: postgres; Tablespace: 
--

CREATE TABLE user_roles (
    id integer NOT NULL,
    employee_id smallint NOT NULL,
    role text NOT NULL
);


ALTER TABLE user_roles OWNER TO postgres;

--
-- Name: COLUMN user_roles.role; Type: COMMENT; Schema: ess; Owner: postgres
--

COMMENT ON COLUMN user_roles.role IS 'A role given to this employee. Should match a valid role in ESS backend.';


--
-- Name: user_roles_id_seq; Type: SEQUENCE; Schema: ess; Owner: postgres
--

CREATE SEQUENCE user_roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE user_roles_id_seq OWNER TO postgres;

--
-- Name: user_roles_id_seq; Type: SEQUENCE OWNED BY; Schema: ess; Owner: postgres
--

ALTER SEQUENCE user_roles_id_seq OWNED BY user_roles.id;


--
-- Name: id; Type: DEFAULT; Schema: ess; Owner: postgres
--

ALTER TABLE ONLY user_roles ALTER COLUMN id SET DEFAULT nextval('user_roles_id_seq'::regclass);


--
-- Name: user_roles_pkey; Type: CONSTRAINT; Schema: ess; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (id);

--
-- Add permissions for all roles.
--
GRANT ALL PRIVILEGES ON SCHEMA ess TO PUBLIC;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA ess TO PUBLIC;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA ess TO PUBLIC;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA ess TO PUBLIC;

--
-- PostgreSQL database dump complete
--

