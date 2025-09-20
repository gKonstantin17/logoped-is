-- Database: Logoped
-- DROP DATABASE IF EXISTS "Logoped";
CREATE DATABASE "Logoped"
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'English_United States.1252'
    LC_CTYPE = 'English_United States.1252'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

-- SCHEMA: notification
-- DROP SCHEMA IF EXISTS notification ;

CREATE SCHEMA IF NOT EXISTS notification
    AUTHORIZATION postgres;
--
--	1. LessonNote
--
CREATE TABLE IF NOT EXISTS notification."LessonNote"
(
    "Id" bigint NOT NULL,
    "Status" text COLLATE pg_catalog."default",
    "StartTime" timestamp without time zone,
    "LogopedId" uuid,
    CONSTRAINT "Lesson_pkey" PRIMARY KEY ("Id")
)
--
--	2. Recipient
--
CREATE TABLE IF NOT EXISTS notification."Recipient"
(
    "Id" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    "PatientId" bigint,
    "UserId" uuid,
    "LessonNoteId" bigint,
    CONSTRAINT "Recipient_pkey" PRIMARY KEY ("Id"),
    CONSTRAINT "FK_Recipient_LessonNote" FOREIGN KEY ("LessonNoteId")
        REFERENCES notification."LessonNote" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
--
--	3. Notification
--
CREATE TABLE IF NOT EXISTS notification."Notification"
(
    "Id" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    "LessonNoteId" bigint NOT NULL,
    "SendDate" timestamp without time zone,
    "Message" text COLLATE pg_catalog."default",
    "Received" boolean,
    "RecipientId" uuid NOT NULL,
    "PatientsId" bigint[],
    CONSTRAINT "Notification_pkey" PRIMARY KEY ("Id"),
    CONSTRAINT "FK_notification_lessonNote" FOREIGN KEY ("LessonNoteId")
        REFERENCES notification."LessonNote" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
