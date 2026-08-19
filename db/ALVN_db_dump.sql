--
-- PostgreSQL database dump
--

\restrict mOwRtGoW2is9irrJqWSI7VgPTA1eddltUbJ0S3okZiZ7ONPWjXD5c9mdbqR8poj

-- Dumped from database version 18.0
-- Dumped by pg_dump version 18.0

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

-- *not* creating schema, since initdb creates it


ALTER SCHEMA public OWNER TO postgres;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS '';


--
-- Name: extract_all_part_ids(jsonb); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.extract_all_part_ids(cfg jsonb) RETURNS integer[]
    LANGUAGE plpgsql IMMUTABLE STRICT
    AS $$
DECLARE
    base_ids int[];
    add_ids int[];
    exclude_ids int[];
BEGIN
    -- Извлекаем массивы из разных веток JSONB
    base_ids := ARRAY(SELECT jsonb_array_elements_text(coalesce(cfg->'base_part_ids', '[]'::jsonb))::int);
    add_ids := ARRAY(SELECT jsonb_array_elements_text(coalesce(cfg->'options'->'add_parts', '[]'::jsonb))::int);
    exclude_ids := ARRAY(SELECT jsonb_array_elements_text(coalesce(cfg->'options'->'exclude_parts', '[]'::jsonb))::int);
    
    -- Объединяем все массивы в один плоский список (используя встроенный оператор конкатенации массивов)
    RETURN ARRAY(
        SELECT DISTINCT unnest(base_ids || add_ids || exclude_ids)
    );
END;
$$;


ALTER FUNCTION public.extract_all_part_ids(cfg jsonb) OWNER TO postgres;

--
-- Name: get_config_by_params(integer, integer, integer, integer, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_config_by_params(p_type_id integer DEFAULT NULL::integer, p_size_id integer DEFAULT NULL::integer, p_side_id integer DEFAULT NULL::integer, p_weight_id integer DEFAULT NULL::integer, p_color_id integer DEFAULT NULL::integer, p_option_id integer DEFAULT NULL::integer) RETURNS TABLE(part_id integer, article character varying, part_name character varying, quantity integer, color_name character varying, hex_code character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_base_parts JSONB;
    v_add_parts JSONB;
    v_exclude_parts JSONB;
    v_color_name VARCHAR(60);
    v_hex_code VARCHAR(6);
BEGIN
    -- Получаем цвет если он выбран
    IF p_color_id IS NOT NULL THEN
        SELECT c.color_name, c.hex_code INTO v_color_name, v_hex_code
        FROM colors c
        WHERE c.id_color = p_color_id;
    END IF;

    -- Собираем все base_part_ids из правил
    WITH all_rules AS (
        SELECT 
            config->'base_part_ids' AS base_parts
        FROM config
        WHERE 
            (config_id_type = p_type_id OR config_id_type IS NULL)
            AND (config_id_size = p_size_id OR config_id_size IS NULL)
            AND (config_id_side = p_side_id OR config_id_side IS NULL)
            AND (config_id_weight = p_weight_id OR config_id_weight IS NULL)
            AND (config_id_color = p_color_id OR config_id_color IS NULL)
            AND (config_id_option = p_option_id OR config_id_option IS NULL)
            AND config->'base_part_ids' IS NOT NULL
            AND jsonb_array_length(config->'base_part_ids') > 0
    )
    SELECT 
        jsonb_agg(DISTINCT elem) INTO v_base_parts
    FROM all_rules, jsonb_array_elements(base_parts) AS elem;

    -- Собираем все add_parts из правил
    WITH add_rules AS (
        SELECT 
            config->'options'->'add_parts' AS add_parts
        FROM config
        WHERE 
            (config_id_type = p_type_id OR config_id_type IS NULL)
            AND (config_id_size = p_size_id OR config_id_size IS NULL)
            AND (config_id_side = p_side_id OR config_id_side IS NULL)
            AND (config_id_weight = p_weight_id OR config_id_weight IS NULL)
            AND (config_id_color = p_color_id OR config_id_color IS NULL)
            AND (config_id_option = p_option_id OR config_id_option IS NULL)
            AND config->'options'->'add_parts' IS NOT NULL
            AND jsonb_array_length(config->'options'->'add_parts') > 0
    )
    SELECT 
        jsonb_agg(DISTINCT elem) INTO v_add_parts
    FROM add_rules, jsonb_array_elements(add_parts) AS elem;

    -- Собираем все exclude_parts из правил
    WITH exclude_rules AS (
        SELECT 
            config->'options'->'exclude_parts' AS exclude_parts
        FROM config
        WHERE 
            (config_id_type = p_type_id OR config_id_type IS NULL)
            AND (config_id_size = p_size_id OR config_id_size IS NULL)
            AND (config_id_side = p_side_id OR config_id_side IS NULL)
            AND (config_id_weight = p_weight_id OR config_id_weight IS NULL)
            AND (config_id_color = p_color_id OR config_id_color IS NULL)
            AND (config_id_option = p_option_id OR config_id_option IS NULL)
            AND config->'options'->'exclude_parts' IS NOT NULL
            AND jsonb_array_length(config->'options'->'exclude_parts') > 0
    )
    SELECT 
        jsonb_agg(DISTINCT elem) INTO v_exclude_parts
    FROM exclude_rules, jsonb_array_elements(exclude_parts) AS elem;

    -- Создаем временную таблицу для сбора деталей
    CREATE TEMP TABLE temp_parts (
        part_id_tmp INT,
        quantity_tmp INT
    ) ON COMMIT DROP;

    -- Вставляем base_parts
    IF v_base_parts IS NOT NULL THEN
        INSERT INTO temp_parts (part_id_tmp, quantity_tmp)
        SELECT 
            (elem->>'id')::INT,
            (elem->>'q')::INT
        FROM jsonb_array_elements(v_base_parts) AS elem;
    END IF;

    -- Добавляем add_parts
    IF v_add_parts IS NOT NULL THEN
        INSERT INTO temp_parts (part_id_tmp, quantity_tmp)
        SELECT 
            (elem->>'id')::INT,
            (elem->>'q')::INT
        FROM jsonb_array_elements(v_add_parts) AS elem;
    END IF;

    -- Убираем exclude_parts
    IF v_exclude_parts IS NOT NULL THEN
        DELETE FROM temp_parts
        WHERE part_id_tmp IN (
            SELECT (elem->>'id')::INT
            FROM jsonb_array_elements(v_exclude_parts) AS elem
        );
    END IF;

    -- Возвращаем результат с деталями из таблицы parts
    RETURN QUERY
    SELECT 
        tp.part_id_tmp,
        p.article,
        p.part_name,
        SUM(tp.quantity_tmp)::INT AS quantity,
        v_color_name,
        v_hex_code
    FROM temp_parts tp
    JOIN parts p ON tp.part_id_tmp = p.id_part
    GROUP BY tp.part_id_tmp, p.article, p.part_name
    ORDER BY p.article;
END;
$$;


ALTER FUNCTION public.get_config_by_params(p_type_id integer, p_size_id integer, p_side_id integer, p_weight_id integer, p_color_id integer, p_option_id integer) OWNER TO postgres;

--
-- Name: verify_config_parts(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.verify_config_parts() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
    part_ids int[];
BEGIN
    -- Извлекаем все ID из base_part_ids
    SELECT ARRAY(
        SELECT (elem->>'id')::int
        FROM jsonb_array_elements(NEW.config->'base_part_ids') AS elem
    ) INTO part_ids;
    
    -- Если массив пустой, проверять нечего
    IF part_ids = '{}'::int[] THEN
        RETURN NEW;
    END IF;

    -- Проверяем, что абсолютно все извлеченные ID существуют физически
    IF NOT (SELECT array_agg(id_part) FROM parts WHERE id_part = ANY(part_ids)) @> part_ids THEN
        RAISE EXCEPTION 'Ошибка целостности: какая-то из деталей не существует в таблице parts';
    END IF;
    
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.verify_config_parts() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: part_colors; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.part_colors (
    id_color integer CONSTRAINT colors_id_color_not_null NOT NULL,
    color_name character varying(60),
    hex_code character varying(7)
);


ALTER TABLE public.part_colors OWNER TO postgres;

--
-- Name: colors_id_color_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.colors_id_color_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.colors_id_color_seq OWNER TO postgres;

--
-- Name: colors_id_color_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.colors_id_color_seq OWNED BY public.part_colors.id_color;


--
-- Name: config; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.config (
    id_config integer NOT NULL,
    config_id_type integer,
    config_id_size integer,
    config_id_side integer,
    config_id_weight integer,
    config_id_color integer,
    config_id_option integer,
    config jsonb
);


ALTER TABLE public.config OWNER TO postgres;

--
-- Name: config_id_config_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.config_id_config_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.config_id_config_seq OWNER TO postgres;

--
-- Name: config_id_config_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.config_id_config_seq OWNED BY public.config.id_config;


--
-- Name: furniture_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.furniture_type (
    id_type integer CONSTRAINT part_type_id_type_not_null NOT NULL,
    furniture_type character varying(60)
);


ALTER TABLE public.furniture_type OWNER TO postgres;

--
-- Name: part_options; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.part_options (
    id_option integer NOT NULL,
    option_name character varying(100)
);


ALTER TABLE public.part_options OWNER TO postgres;

--
-- Name: part_options_id_option_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.part_options_id_option_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.part_options_id_option_seq OWNER TO postgres;

--
-- Name: part_options_id_option_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.part_options_id_option_seq OWNED BY public.part_options.id_option;


--
-- Name: part_side; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.part_side (
    id_side integer NOT NULL,
    part_side character varying(20)
);


ALTER TABLE public.part_side OWNER TO postgres;

--
-- Name: part_side_id_side_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.part_side_id_side_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.part_side_id_side_seq OWNER TO postgres;

--
-- Name: part_side_id_side_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.part_side_id_side_seq OWNED BY public.part_side.id_side;


--
-- Name: part_type_id_type_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.part_type_id_type_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.part_type_id_type_seq OWNER TO postgres;

--
-- Name: part_type_id_type_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.part_type_id_type_seq OWNED BY public.furniture_type.id_type;


--
-- Name: part_weight; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.part_weight (
    id_type integer NOT NULL,
    part_weight character varying(60)
);


ALTER TABLE public.part_weight OWNER TO postgres;

--
-- Name: part_weight_id_type_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.part_weight_id_type_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.part_weight_id_type_seq OWNER TO postgres;

--
-- Name: part_weight_id_type_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.part_weight_id_type_seq OWNED BY public.part_weight.id_type;


--
-- Name: parts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.parts (
    id_part integer NOT NULL,
    part_name character varying(100),
    article character varying(40)
);


ALTER TABLE public.parts OWNER TO postgres;

--
-- Name: parts_id_part_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.parts_id_part_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.parts_id_part_seq OWNER TO postgres;

--
-- Name: parts_id_part_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.parts_id_part_seq OWNED BY public.parts.id_part;


--
-- Name: sizes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sizes (
    id_size integer NOT NULL,
    max_height integer,
    max_width integer
);


ALTER TABLE public.sizes OWNER TO postgres;

--
-- Name: sizes_id_size_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.sizes_id_size_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sizes_id_size_seq OWNER TO postgres;

--
-- Name: sizes_id_size_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.sizes_id_size_seq OWNED BY public.sizes.id_size;


--
-- Name: config id_config; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.config ALTER COLUMN id_config SET DEFAULT nextval('public.config_id_config_seq'::regclass);


--
-- Name: furniture_type id_type; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.furniture_type ALTER COLUMN id_type SET DEFAULT nextval('public.part_type_id_type_seq'::regclass);


--
-- Name: part_colors id_color; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.part_colors ALTER COLUMN id_color SET DEFAULT nextval('public.colors_id_color_seq'::regclass);


--
-- Name: part_options id_option; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.part_options ALTER COLUMN id_option SET DEFAULT nextval('public.part_options_id_option_seq'::regclass);


--
-- Name: part_side id_side; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.part_side ALTER COLUMN id_side SET DEFAULT nextval('public.part_side_id_side_seq'::regclass);


--
-- Name: part_weight id_type; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.part_weight ALTER COLUMN id_type SET DEFAULT nextval('public.part_weight_id_type_seq'::regclass);


--
-- Name: parts id_part; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.parts ALTER COLUMN id_part SET DEFAULT nextval('public.parts_id_part_seq'::regclass);


--
-- Name: sizes id_size; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sizes ALTER COLUMN id_size SET DEFAULT nextval('public.sizes_id_size_seq'::regclass);


--
-- Data for Name: config; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.config (id_config, config_id_type, config_id_size, config_id_side, config_id_weight, config_id_color, config_id_option, config) FROM stdin;
284	1	1	\N	\N	\N	\N	{"addParts": null, "basePart": null, "deleteParts": null}
285	1	2	\N	\N	\N	\N	{"addParts": null, "basePart": [{"q": 1, "id": 9}, {"q": 1, "id": 4}, {"q": 1, "id": 10}], "deleteParts": null}
286	1	3	\N	\N	\N	\N	{"addParts": null, "basePart": [{"q": 1, "id": 9}, {"q": 3, "id": 4}, {"q": 3, "id": 10}], "deleteParts": null}
287	1	4	\N	\N	\N	\N	{"addParts": null, "basePart": [{"q": 1, "id": 9}, {"q": 5, "id": 4}, {"q": 5, "id": 10}], "deleteParts": null}
288	2	5	\N	\N	\N	\N	{"addParts": null, "basePart": [{"q": 1, "id": 5}, {"q": 1, "id": 3}], "deleteParts": null}
289	2	6	\N	\N	\N	\N	{"addParts": null, "basePart": [{"q": 1, "id": 5}, {"q": 1, "id": 3}, {"q": 1, "id": 13}, {"q": 1, "id": 10}, {"q": 1, "id": 4}], "deleteParts": null}
290	2	7	\N	\N	\N	\N	{"addParts": null, "basePart": [{"q": 1, "id": 5}, {"q": 1, "id": 3}, {"q": 2, "id": 13}, {"q": 2, "id": 10}, {"q": 2, "id": 4}], "deleteParts": null}
291	2	8	\N	\N	\N	\N	{"addParts": null, "basePart": [{"q": 1, "id": 5}, {"q": 1, "id": 3}, {"q": 3, "id": 13}, {"q": 3, "id": 10}, {"q": 3, "id": 4}], "deleteParts": null}
292	3	9	\N	\N	\N	\N	{"addParts": null, "basePart": [{"q": 2, "id": 14}, {"q": 1, "id": 5}, {"q": 1, "id": 3}], "deleteParts": null}
293	3	10	\N	\N	\N	\N	{"addParts": null, "basePart": [{"q": 1, "id": 13}, {"q": 2, "id": 14}, {"q": 1, "id": 5}, {"q": 1, "id": 3}, {"q": 1, "id": 10}, {"q": 1, "id": 4}], "deleteParts": null}
294	3	11	\N	\N	\N	\N	{"addParts": null, "basePart": [{"q": 2, "id": 13}, {"q": 2, "id": 14}, {"q": 1, "id": 5}, {"q": 1, "id": 3}, {"q": 2, "id": 10}, {"q": 2, "id": 4}], "deleteParts": null}
295	\N	\N	\N	\N	1	\N	{"addParts": [{"q": 1, "id": 7}], "basePart": null, "deleteParts": null}
296	\N	\N	\N	\N	2	\N	{"addParts": [{"q": 1, "id": 8}], "basePart": null, "deleteParts": null}
297	\N	\N	\N	\N	3	\N	{"addParts": [{"q": 1, "id": 6}], "basePart": null, "deleteParts": null}
298	1	\N	1	\N	\N	\N	{"addParts": [{"q": 1, "id": 2}], "basePart": null, "deleteParts": null}
299	1	\N	2	\N	\N	\N	{"addParts": [{"q": 1, "id": 1}], "basePart": null, "deleteParts": null}
300	\N	\N	1	3	\N	6	{"addParts": [{"q": 1, "id": 12}], "basePart": null, "deleteParts": null}
301	\N	\N	2	3	\N	7	{"addParts": [{"q": 1, "id": 11}], "basePart": null, "deleteParts": null}
\.


--
-- Data for Name: furniture_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.furniture_type (id_type, furniture_type) FROM stdin;
3	Фрамуга
1	Поворотно откидное
2	Поворотное
\.


--
-- Data for Name: part_colors; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.part_colors (id_color, color_name, hex_code) FROM stdin;
1	Белый	#FFFFFF
2	Черный	#000000
3	Неокрас	\N
\.


--
-- Data for Name: part_options; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.part_options (id_option, option_name) FROM stdin;
7	Разгружатель петель левый
6	Разгружатель петель правый
\.


--
-- Data for Name: part_side; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.part_side (id_side, part_side) FROM stdin;
1	Правая
2	Левая
\.


--
-- Data for Name: part_weight; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.part_weight (id_type, part_weight) FROM stdin;
3	от 120 до 160кг
\.


--
-- Data for Name: parts; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.parts (id_part, part_name, article) FROM stdin;
1	Комплект поворотно-откидной Левый (180/90, 120 кг)	ALV43100 L
2	Комплект поворотно-откидной Правый (180/90, 120 кг)	ALV43100 R
3	Комплект поворотного окна со стороны ручки (включает винты под ручку)	ALVPK
4	Ответная планка	ALVPL
5	Комплект скрытых поворотных петель (180/90, 120 кг)	ALVPP
6	Ручка Оконная 104 мм Неокрас	ALVRU00 - NO Color
7	Ручка Оконная 104 мм Белая	ALVRU01 - White
8	Ручка Оконная 104 мм Черная	ALVRU02 - Black
9	Комплект удлинение по горизонт. низ/сторона петель	ALVZAP
10	Цапфа регулируемая винтовая Aluvisions	511149.1
11	Разгружатель петель левый	ZFC10-L
12	Разгружатель петель правый	ZFC10-R
13	Скрытый прижим скрытый (комплект- рама + створка) для скрытых петель	LSK72
14	Фрамужные ножницы	NDPX-2A
\.


--
-- Data for Name: sizes; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.sizes (id_size, max_height, max_width) FROM stdin;
1	800	1000
2	1300	1000
3	1900	1000
4	2699	1000
5	800	850
6	1300	850
7	1900	850
8	2699	850
9	1000	900
10	1000	1300
11	1000	1600
\.


--
-- Name: colors_id_color_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.colors_id_color_seq', 3, true);


--
-- Name: config_id_config_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.config_id_config_seq', 301, true);


--
-- Name: part_options_id_option_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.part_options_id_option_seq', 7, true);


--
-- Name: part_side_id_side_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.part_side_id_side_seq', 3, true);


--
-- Name: part_type_id_type_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.part_type_id_type_seq', 3, true);


--
-- Name: part_weight_id_type_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.part_weight_id_type_seq', 3, true);


--
-- Name: parts_id_part_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.parts_id_part_seq', 14, true);


--
-- Name: sizes_id_size_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.sizes_id_size_seq', 11, true);


--
-- Name: part_colors colors_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.part_colors
    ADD CONSTRAINT colors_pkey PRIMARY KEY (id_color);


--
-- Name: config config_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.config
    ADD CONSTRAINT config_pkey PRIMARY KEY (id_config);


--
-- Name: part_options part_options_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.part_options
    ADD CONSTRAINT part_options_pkey PRIMARY KEY (id_option);


--
-- Name: part_side part_side_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.part_side
    ADD CONSTRAINT part_side_pkey PRIMARY KEY (id_side);


--
-- Name: furniture_type part_type_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.furniture_type
    ADD CONSTRAINT part_type_pkey PRIMARY KEY (id_type);


--
-- Name: part_weight part_weight_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.part_weight
    ADD CONSTRAINT part_weight_pkey PRIMARY KEY (id_type);


--
-- Name: parts parts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.parts
    ADD CONSTRAINT parts_pkey PRIMARY KEY (id_part);


--
-- Name: sizes sizes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sizes
    ADD CONSTRAINT sizes_pkey PRIMARY KEY (id_size);


--
-- Name: config trg_verify_config_parts; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_verify_config_parts BEFORE INSERT OR UPDATE ON public.config FOR EACH ROW EXECUTE FUNCTION public.verify_config_parts();


--
-- Name: config fk_config_colors; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.config
    ADD CONSTRAINT fk_config_colors FOREIGN KEY (config_id_color) REFERENCES public.part_colors(id_color);


--
-- Name: config fk_config_furniture_type; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.config
    ADD CONSTRAINT fk_config_furniture_type FOREIGN KEY (config_id_type) REFERENCES public.furniture_type(id_type);


--
-- Name: config fk_config_part_options; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.config
    ADD CONSTRAINT fk_config_part_options FOREIGN KEY (config_id_option) REFERENCES public.part_options(id_option);


--
-- Name: config fk_config_part_side; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.config
    ADD CONSTRAINT fk_config_part_side FOREIGN KEY (config_id_side) REFERENCES public.part_side(id_side);


--
-- Name: config fk_config_part_weight; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.config
    ADD CONSTRAINT fk_config_part_weight FOREIGN KEY (config_id_weight) REFERENCES public.part_weight(id_type);


--
-- Name: config fk_config_sizes; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.config
    ADD CONSTRAINT fk_config_sizes FOREIGN KEY (config_id_size) REFERENCES public.sizes(id_size);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

\unrestrict mOwRtGoW2is9irrJqWSI7VgPTA1eddltUbJ0S3okZiZ7ONPWjXD5c9mdbqR8poj

