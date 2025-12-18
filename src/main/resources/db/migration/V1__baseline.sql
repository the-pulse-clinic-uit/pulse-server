--
-- PostgreSQL database dump
--

-- Dumped from database version 18.0 (Debian 18.0-1.pgdg13+3)
-- Dumped by pg_dump version 18.0 (Debian 18.0-1.pgdg13+3)
--ALTER DATABASE "pulse-db" SET timezone TO 'Asia/Ho_Chi_Minh';

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: admissions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.admissions (
    admitted_at timestamp(6) without time zone NOT NULL,
    discharged_at timestamp(6) without time zone,
    deleted_at timestamp(6) without time zone,
    doctor_id uuid NOT NULL,
    encounter_id uuid,
    id uuid NOT NULL,
    patient_id uuid NOT NULL,
    room_id uuid NOT NULL,
    notes text,
    status character varying(255) NOT NULL,
    CONSTRAINT admissions_status_check CHECK (((status)::text = ANY ((ARRAY['ONGOING'::character varying, 'DISCHARGED'::character varying, 'TRANSFERRED'::character varying])::text[])))
);


--
-- Name: appointments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.appointments (
    created_at timestamp(6) without time zone NOT NULL,
    deleted_at timestamp(6) without time zone,
    ends_at timestamp(6) without time zone NOT NULL,
    starts_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone,
    doctor_id uuid NOT NULL,
    follow_up_plan_id uuid,
    id uuid NOT NULL,
    patient_id uuid NOT NULL,
    shift_assignment_id uuid,
    description text,
    status character varying(255) NOT NULL,
    type character varying(255) NOT NULL,
    CONSTRAINT appointments_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'CONFIRMED'::character varying, 'CANCELLED'::character varying, 'NO_SHOW'::character varying, 'DONE'::character varying])::text[]))),
    CONSTRAINT appointments_type_check CHECK (((type)::text = ANY ((ARRAY['NORMAL'::character varying, 'FOLLOW_UP'::character varying, 'EMERGENCY'::character varying])::text[])))
);


--
-- Name: departments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.departments (
    created_at timestamp(6) without time zone NOT NULL,
    deleted_at timestamp(6) without time zone,
    id uuid NOT NULL,
    name character varying(50) NOT NULL,
    description text NOT NULL
);


--
-- Name: doctors; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.doctors (
    is_verified boolean DEFAULT false,
    created_at timestamp(6) without time zone NOT NULL,
    deleted_at timestamp(6) without time zone,
    department_id uuid,
    id uuid NOT NULL,
    staff_id uuid,
    license_id character varying(50) NOT NULL
);


--
-- Name: drugs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.drugs (
    unit_price numeric(38,2) DEFAULT 0.00 NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    deleted_at timestamp(6) without time zone,
    id uuid NOT NULL,
    dosage_form character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    strength character varying(255) NOT NULL,
    unit character varying(255) NOT NULL,
    CONSTRAINT drugs_dosage_form_check CHECK (((dosage_form)::text = ANY ((ARRAY['TABLET'::character varying, 'CAPSULE'::character varying, 'SYRUP'::character varying, 'INJECTION'::character varying, 'CREAM'::character varying, 'OINTMENT'::character varying, 'GEL'::character varying, 'DROPS'::character varying, 'SUPPOSITORY'::character varying, 'POWDER'::character varying, 'SPRAY'::character varying, 'PATCH'::character varying, 'SOLUTION'::character varying, 'SUSPENSION'::character varying, 'LOTION'::character varying, 'MOUTHWASH'::character varying, 'INHALER'::character varying])::text[]))),
    CONSTRAINT drugs_unit_check CHECK (((unit)::text = ANY ((ARRAY['TABLET'::character varying, 'CAPSULE'::character varying, 'ML'::character varying, 'G'::character varying, 'AMP'::character varying, 'VIAL'::character varying, 'BOTTLE'::character varying, 'TUBE'::character varying, 'BOX'::character varying, 'PACK'::character varying, 'STRIP'::character varying])::text[])))
);


--
-- Name: encounters; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.encounters (
    created_at timestamp(6) without time zone NOT NULL,
    deleted_at timestamp(6) without time zone,
    ended_at timestamp(6) without time zone NOT NULL,
    started_at timestamp(6) without time zone NOT NULL,
    appointment_id uuid,
    doctor_id uuid NOT NULL,
    id uuid NOT NULL,
    patient_id uuid NOT NULL,
    diagnosis text NOT NULL,
    notes text NOT NULL,
    type character varying(255) NOT NULL,
    CONSTRAINT encounters_type_check CHECK (((type)::text = ANY ((ARRAY['EMERGENCY'::character varying, 'APPOINTED'::character varying])::text[])))
);


--
-- Name: follow_up_plans; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.follow_up_plans (
    created_at timestamp(6) without time zone NOT NULL,
    deleted_at timestamp(6) without time zone,
    first_due_at timestamp(6) without time zone NOT NULL,
    base_encounter_id uuid NOT NULL,
    doctor_id uuid NOT NULL,
    id uuid NOT NULL,
    patient_id uuid NOT NULL,
    notes text,
    rrule text NOT NULL,
    status character varying(255) NOT NULL,
    CONSTRAINT follow_up_plans_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'PAUSED'::character varying, 'COMPLETED'::character varying])::text[])))
);


--
-- Name: invoices; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.invoices (
    amount_paid numeric(38,2) DEFAULT 0.00 NOT NULL,
    due_date date NOT NULL,
    total_amount numeric(38,2) DEFAULT 0.00 NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    deleted_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    encounter_id uuid NOT NULL,
    id uuid NOT NULL,
    status character varying(255) NOT NULL,
    CONSTRAINT invoices_status_check CHECK (((status)::text = ANY ((ARRAY['PAID'::character varying, 'UNPAID'::character varying, 'VOID'::character varying, 'PARTIAL'::character varying])::text[])))
);


--
-- Name: patients; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.patients (
    created_at timestamp(6) without time zone NOT NULL,
    deleted_at timestamp(6) without time zone,
    id uuid NOT NULL,
    user_id uuid,
    health_insurance_id character varying(64) NOT NULL,
    allergies text,
    blood_type character varying(255) NOT NULL,
    CONSTRAINT patients_blood_type_check CHECK (((blood_type)::text = ANY ((ARRAY['A'::character varying, 'B'::character varying, 'AB'::character varying, 'O'::character varying, 'A_neg'::character varying, 'B_neg'::character varying, 'O_neg'::character varying, 'AB_neg'::character varying])::text[])))
);


--
-- Name: prescription_details; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.prescription_details (
    item_total_price numeric(38,2) DEFAULT 0.00 NOT NULL,
    quantity integer DEFAULT 0 NOT NULL,
    unit_price numeric(38,2) DEFAULT 0.00 NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    deleted_at timestamp(6) without time zone,
    drug_id uuid NOT NULL,
    id uuid NOT NULL,
    prescription_id uuid NOT NULL,
    dose character varying(255) NOT NULL,
    frequency character varying(255) NOT NULL,
    instructions text NOT NULL,
    strength_text text,
    timing character varying(255) NOT NULL
);


--
-- Name: prescriptions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.prescriptions (
    status smallint NOT NULL,
    total_price numeric(38,2) DEFAULT 0.00 NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    deleted_at timestamp(6) without time zone,
    encounter_id uuid NOT NULL,
    id uuid NOT NULL,
    notes text NOT NULL,
    CONSTRAINT prescriptions_status_check CHECK (((status >= 0) AND (status <= 2)))
);


--
-- Name: roles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.roles (
    created_at timestamp(6) without time zone NOT NULL,
    deleted_at timestamp(6) without time zone,
    id uuid NOT NULL,
    name character varying(50) NOT NULL
);


--
-- Name: rooms; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.rooms (
    bed_amount integer DEFAULT 1 NOT NULL,
    is_available boolean DEFAULT true,
    room_number character varying(4) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    deleted_at timestamp(6) without time zone,
    department_id uuid,
    id uuid NOT NULL
);


--
-- Name: shift_assignments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.shift_assignments (
    duty_date date NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    deleted_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    doctor_id uuid NOT NULL,
    id uuid NOT NULL,
    room_id uuid,
    shift_id uuid NOT NULL,
    notes text,
    role_in_shift character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    CONSTRAINT shift_assignments_role_in_shift_check CHECK (((role_in_shift)::text = ANY ((ARRAY['ON_CALL'::character varying, 'PRIMARY'::character varying])::text[]))),
    CONSTRAINT shift_assignments_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'CANCELLED'::character varying])::text[])))
);


--
-- Name: shifts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.shifts (
    capacity_per_slot integer DEFAULT 1 NOT NULL,
    slot_minutes integer DEFAULT 30 NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    deleted_at timestamp(6) without time zone,
    end_time timestamp(6) without time zone NOT NULL,
    start_time timestamp(6) without time zone NOT NULL,
    default_room_id uuid,
    department_id uuid,
    id uuid NOT NULL,
    name character varying(50) NOT NULL,
    kind character varying(255) NOT NULL,
    CONSTRAINT shifts_kind_check CHECK (((kind)::text = ANY ((ARRAY['ER'::character varying, 'CLINIC'::character varying])::text[])))
);


--
-- Name: staff; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.staff (
    created_at timestamp(6) without time zone NOT NULL,    deleted_at timestamp(6) without time zone,    id uuid NOT NULL,
    user_id uuid,
    "position" character varying(255) NOT NULL,
    CONSTRAINT staff_position_check CHECK ((("position")::text = ANY ((ARRAY['DOCTOR'::character varying, 'STAFF'::character varying])::text[])))
);


--
-- Name: staff_ratings; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.staff_ratings (
    rating integer NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    deleted_at timestamp(6) without time zone,
    encounter_id uuid,
    id uuid NOT NULL,
    patient_id uuid,
    staff_id uuid NOT NULL,
    comment character varying(256) NOT NULL,
    guest_contact_hash character varying(255),
    guest_contact_type character varying(255) NOT NULL,
    rater_type character varying(255) NOT NULL,
    CONSTRAINT staff_ratings_rater_type_check CHECK (((rater_type)::text = ANY ((ARRAY['PATIENT'::character varying, 'GUEST'::character varying])::text[])))
);


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    birth_date date,
    gender boolean DEFAULT true,
    is_active boolean DEFAULT true NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    deleted_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone NOT NULL,
    phone character varying(12),
    id uuid NOT NULL,
    role_id uuid,
    citizen_id character varying(32) NOT NULL,
    address character varying(500),
    avatar_url character varying(512),
    email character varying(255) NOT NULL,
    full_name character varying(255) NOT NULL,
    hashed_password character varying(255) NOT NULL
);


--
-- Name: waitlist_entries; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.waitlist_entries (
    duty_date date NOT NULL,
    ticket_np integer,
    called_at timestamp(6) without time zone,
    created_at timestamp(6) without time zone NOT NULL,
    deleted_at timestamp(6) without time zone,
    served_at timestamp(6) without time zone,
    appointment_id uuid,
    doctor_id uuid NOT NULL,
    id uuid NOT NULL,
    patient_id uuid NOT NULL,
    notes text,
    priority character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    CONSTRAINT waitlist_entries_priority_check CHECK (((priority)::text = ANY ((ARRAY['NORMAL'::character varying, 'PRIORITY'::character varying, 'EMERGENCY'::character varying])::text[]))),
    CONSTRAINT waitlist_entries_status_check CHECK (((status)::text = ANY ((ARRAY['WAITING'::character varying, 'CALLED'::character varying, 'SERVED'::character varying, 'NO_SHOW'::character varying, 'CANCELLED'::character varying])::text[])))
);


--
-- Name: admissions admissions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.admissions
    ADD CONSTRAINT admissions_pkey PRIMARY KEY (id);


--
-- Name: appointments appointments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.appointments
    ADD CONSTRAINT appointments_pkey PRIMARY KEY (id);


--
-- Name: departments departments_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.departments
    ADD CONSTRAINT departments_name_key UNIQUE (name);


--
-- Name: departments departments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.departments
    ADD CONSTRAINT departments_pkey PRIMARY KEY (id);


--
-- Name: doctors doctors_license_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.doctors
    ADD CONSTRAINT doctors_license_id_key UNIQUE (license_id);


--
-- Name: doctors doctors_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.doctors
    ADD CONSTRAINT doctors_pkey PRIMARY KEY (id);


--
-- Name: doctors doctors_staff_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.doctors
    ADD CONSTRAINT doctors_staff_id_key UNIQUE (staff_id);


--
-- Name: drugs drugs_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.drugs
    ADD CONSTRAINT drugs_name_key UNIQUE (name);


--
-- Name: drugs drugs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.drugs
    ADD CONSTRAINT drugs_pkey PRIMARY KEY (id);


--
-- Name: encounters encounters_appointment_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.encounters
    ADD CONSTRAINT encounters_appointment_id_key UNIQUE (appointment_id);


--
-- Name: encounters encounters_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.encounters
    ADD CONSTRAINT encounters_pkey PRIMARY KEY (id);


--
-- Name: follow_up_plans follow_up_plans_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.follow_up_plans
    ADD CONSTRAINT follow_up_plans_pkey PRIMARY KEY (id);


--
-- Name: invoices invoices_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.invoices
    ADD CONSTRAINT invoices_pkey PRIMARY KEY (id);


--
-- Name: patients patients_health_insurance_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.patients
    ADD CONSTRAINT patients_health_insurance_id_key UNIQUE (health_insurance_id);


--
-- Name: patients patients_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.patients
    ADD CONSTRAINT patients_pkey PRIMARY KEY (id);


--
-- Name: patients patients_user_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.patients
    ADD CONSTRAINT patients_user_id_key UNIQUE (user_id);


--
-- Name: prescription_details prescription_details_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.prescription_details
    ADD CONSTRAINT prescription_details_pkey PRIMARY KEY (id);


--
-- Name: prescriptions prescriptions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.prescriptions
    ADD CONSTRAINT prescriptions_pkey PRIMARY KEY (id);


--
-- Name: roles roles_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_name_key UNIQUE (name);


--
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- Name: rooms rooms_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rooms
    ADD CONSTRAINT rooms_pkey PRIMARY KEY (id);


--
-- Name: rooms rooms_room_number_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rooms
    ADD CONSTRAINT rooms_room_number_key UNIQUE (room_number);


--
-- Name: shift_assignments shift_assignments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shift_assignments
    ADD CONSTRAINT shift_assignments_pkey PRIMARY KEY (id);


--
-- Name: shifts shifts_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shifts
    ADD CONSTRAINT shifts_name_key UNIQUE (name);


--
-- Name: shifts shifts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shifts
    ADD CONSTRAINT shifts_pkey PRIMARY KEY (id);


--
-- Name: staff staff_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.staff
    ADD CONSTRAINT staff_pkey PRIMARY KEY (id);


--
-- Name: staff_ratings staff_ratings_encounter_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.staff_ratings
    ADD CONSTRAINT staff_ratings_encounter_id_key UNIQUE (encounter_id);


--
-- Name: staff_ratings staff_ratings_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.staff_ratings
    ADD CONSTRAINT staff_ratings_pkey PRIMARY KEY (id);


--
-- Name: staff staff_user_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.staff
    ADD CONSTRAINT staff_user_id_key UNIQUE (user_id);


--
-- Name: users users_citizen_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_citizen_id_key UNIQUE (citizen_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: waitlist_entries waitlist_entries_appointment_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.waitlist_entries
    ADD CONSTRAINT waitlist_entries_appointment_id_key UNIQUE (appointment_id);


--
-- Name: waitlist_entries waitlist_entries_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.waitlist_entries
    ADD CONSTRAINT waitlist_entries_pkey PRIMARY KEY (id);


--
-- Name: invoices fk1e83kn0p0wvhigpgp0e3y3vs9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.invoices
    ADD CONSTRAINT fk1e83kn0p0wvhigpgp0e3y3vs9 FOREIGN KEY (encounter_id) REFERENCES public.encounters(id);


--
-- Name: shift_assignments fk1n8d86cusos3j25ks024ux1oo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shift_assignments
    ADD CONSTRAINT fk1n8d86cusos3j25ks024ux1oo FOREIGN KEY (doctor_id) REFERENCES public.doctors(id);


--
-- Name: shift_assignments fk21yymh6e7f9jgjt80yb8s98r; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shift_assignments
    ADD CONSTRAINT fk21yymh6e7f9jgjt80yb8s98r FOREIGN KEY (shift_id) REFERENCES public.shifts(id);


--
-- Name: follow_up_plans fk2uw445rlws8le0xa16lu9yx2h; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.follow_up_plans
    ADD CONSTRAINT fk2uw445rlws8le0xa16lu9yx2h FOREIGN KEY (patient_id) REFERENCES public.patients(id);


--
-- Name: staff_ratings fk3uejnpftcgyeamcuy02kv6ulp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.staff_ratings
    ADD CONSTRAINT fk3uejnpftcgyeamcuy02kv6ulp FOREIGN KEY (patient_id) REFERENCES public.patients(id);


--
-- Name: encounters fk40145xu8iu6aevo4wp9durw5x; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.encounters
    ADD CONSTRAINT fk40145xu8iu6aevo4wp9durw5x FOREIGN KEY (appointment_id) REFERENCES public.appointments(id);


--
-- Name: prescriptions fk5brauc91c7omvqhemvtly1sop; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.prescriptions
    ADD CONSTRAINT fk5brauc91c7omvqhemvtly1sop FOREIGN KEY (encounter_id) REFERENCES public.encounters(id);


--
-- Name: shift_assignments fk7joa0wil721335m4eeactg2tu; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shift_assignments
    ADD CONSTRAINT fk7joa0wil721335m4eeactg2tu FOREIGN KEY (room_id) REFERENCES public.rooms(id);


--
-- Name: follow_up_plans fk7vgryj3ywkr2mr32vxdwe3di2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.follow_up_plans
    ADD CONSTRAINT fk7vgryj3ywkr2mr32vxdwe3di2 FOREIGN KEY (base_encounter_id) REFERENCES public.encounters(id);


--
-- Name: appointments fk8exap5wmg8kmb1g1rx3by21yt; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.appointments
    ADD CONSTRAINT fk8exap5wmg8kmb1g1rx3by21yt FOREIGN KEY (patient_id) REFERENCES public.patients(id);


--
-- Name: admissions fk8kge86qfquat1w81lxh8cec16; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.admissions
    ADD CONSTRAINT fk8kge86qfquat1w81lxh8cec16 FOREIGN KEY (doctor_id) REFERENCES public.doctors(id);


--
-- Name: appointments fk997ckhlni48hpt2uddouyr682; Type: FK CONSTRAINT; Schema: public; Owner: -
--



--
-- Name: admissions fk9urk28780vytphgfv0t8lpd8v; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.admissions
    ADD CONSTRAINT fk9urk28780vytphgfv0t8lpd8v FOREIGN KEY (encounter_id) REFERENCES public.encounters(id);


--
-- Name: encounters fkayk66ui4qce1mg069e4qfykg7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.encounters
    ADD CONSTRAINT fkayk66ui4qce1mg069e4qfykg7 FOREIGN KEY (patient_id) REFERENCES public.patients(id);


--
-- Name: waitlist_entries fkdb1qe48a311fbajwg556fos65; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.waitlist_entries
    ADD CONSTRAINT fkdb1qe48a311fbajwg556fos65 FOREIGN KEY (appointment_id) REFERENCES public.appointments(id);


--
-- Name: waitlist_entries fkde5xratqwa14xe31p71crtsde; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.waitlist_entries
    ADD CONSTRAINT fkde5xratqwa14xe31p71crtsde FOREIGN KEY (patient_id) REFERENCES public.patients(id);


--
-- Name: staff fkdlvw23ak3u9v9bomm8g12rtc0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.staff
    ADD CONSTRAINT fkdlvw23ak3u9v9bomm8g12rtc0 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: follow_up_plans fkdp7s58u7eyeeahayeyk1dgkna; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.follow_up_plans
    ADD CONSTRAINT fkdp7s58u7eyeeahayeyk1dgkna FOREIGN KEY (doctor_id) REFERENCES public.doctors(id);


--
-- Name: staff_ratings fkdutrkean90b3bf5r9ponip9mx; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.staff_ratings
    ADD CONSTRAINT fkdutrkean90b3bf5r9ponip9mx FOREIGN KEY (encounter_id) REFERENCES public.encounters(id);


--
-- Name: doctors fkdxp9whgsgs0xj66u328li0ye2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.doctors
    ADD CONSTRAINT fkdxp9whgsgs0xj66u328li0ye2 FOREIGN KEY (staff_id) REFERENCES public.staff(id);


--
-- Name: shifts fkfalfj5kldqkp1mol31gubssrq; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shifts
    ADD CONSTRAINT fkfalfj5kldqkp1mol31gubssrq FOREIGN KEY (department_id) REFERENCES public.departments(id);


--
-- Name: staff_ratings fkg0a7n5o2xqv1lom85na2jq1u5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.staff_ratings
    ADD CONSTRAINT fkg0a7n5o2xqv1lom85na2jq1u5 FOREIGN KEY (staff_id) REFERENCES public.staff(id);


--
-- Name: appointments fkhjr22litlb5su6yuldsy6wjdc; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.appointments
    ADD CONSTRAINT fkhjr22litlb5su6yuldsy6wjdc FOREIGN KEY (follow_up_plan_id) REFERENCES public.follow_up_plans(id);


--
-- Name: appointments fkhlp1ok42f40v7d8mnrq6fao6b; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.appointments
    ADD CONSTRAINT fkhlp1ok42f40v7d8mnrq6fao6b FOREIGN KEY (shift_assignment_id) REFERENCES public.shift_assignments(id);


--
-- Name: admissions fkjaaiv076g2j9pina6yskmv8hy; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.admissions
    ADD CONSTRAINT fkjaaiv076g2j9pina6yskmv8hy FOREIGN KEY (room_id) REFERENCES public.rooms(id);


--
-- Name: admissions fkkyky6a6qqfqwfvd92qpopwepy; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.admissions
    ADD CONSTRAINT fkkyky6a6qqfqwfvd92qpopwepy FOREIGN KEY (patient_id) REFERENCES public.patients(id);


--
-- Name: doctors fkl2mro81neln9topymd898urh1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.doctors
    ADD CONSTRAINT fkl2mro81neln9topymd898urh1 FOREIGN KEY (department_id) REFERENCES public.departments(id);


--
-- Name: waitlist_entries fkl4a8tgenfif8tawpj1hsv514m; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.waitlist_entries
    ADD CONSTRAINT fkl4a8tgenfif8tawpj1hsv514m FOREIGN KEY (doctor_id) REFERENCES public.doctors(id);


--
-- Name: encounters fkmgx7gtqfllaa3scxg5k9e2y60; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.encounters
    ADD CONSTRAINT fkmgx7gtqfllaa3scxg5k9e2y60 FOREIGN KEY (doctor_id) REFERENCES public.doctors(id);


--
-- Name: rooms fkmnnwsm0xvdd30vps6hpm92nm6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rooms
    ADD CONSTRAINT fkmnnwsm0xvdd30vps6hpm92nm6 FOREIGN KEY (department_id) REFERENCES public.departments(id);


--
-- Name: appointments fkmujeo4tymoo98cmf7uj3vsv76; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.appointments
    ADD CONSTRAINT fkmujeo4tymoo98cmf7uj3vsv76 FOREIGN KEY (doctor_id) REFERENCES public.doctors(id);


--
-- Name: shifts fkmyed5rbdro6rlaykg7ad3nne1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shifts
    ADD CONSTRAINT fkmyed5rbdro6rlaykg7ad3nne1 FOREIGN KEY (default_room_id) REFERENCES public.rooms(id);


--
-- Name: users fkp56c1712k691lhsyewcssf40f; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fkp56c1712k691lhsyewcssf40f FOREIGN KEY (role_id) REFERENCES public.roles(id);


--
-- Name: prescription_details fkrkvuqqmq8q7157tclr9dns23w; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.prescription_details
    ADD CONSTRAINT fkrkvuqqmq8q7157tclr9dns23w FOREIGN KEY (drug_id) REFERENCES public.drugs(id);


--
-- Name: prescription_details fksw4dl29fglymg5hmic0we7gh; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.prescription_details
    ADD CONSTRAINT fksw4dl29fglymg5hmic0we7gh FOREIGN KEY (prescription_id) REFERENCES public.prescriptions(id);


--
-- Name: patients fkuwca24wcd1tg6pjex8lmc0y7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.patients
    ADD CONSTRAINT fkuwca24wcd1tg6pjex8lmc0y7 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- PostgreSQL database dump complete
--


