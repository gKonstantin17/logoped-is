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

-- SCHEMA: logoped
-- DROP SCHEMA IF EXISTS logoped ;
CREATE SCHEMA IF NOT EXISTS logoped
    AUTHORIZATION postgres;

--
--		1 USER
--
CREATE TABLE IF NOT EXISTS logoped."UserData"
(
    "Id" uuid NOT NULL,
    "FirstName" text COLLATE pg_catalog."default" NOT NULL,
    "LastName" text COLLATE pg_catalog."default" NOT NULL,
    "Email" text COLLATE pg_catalog."default" NOT NULL,
    "Phone" text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT "UserData_pkey" PRIMARY KEY ("Id")
)
--
--		2 Logoped
--
CREATE TABLE IF NOT EXISTS logoped."Logoped"
(
    "Id" uuid NOT NULL,
    "FirstName" text COLLATE pg_catalog."default" NOT NULL,
    "LastName" text COLLATE pg_catalog."default" NOT NULL,
    "Email" text COLLATE pg_catalog."default" NOT NULL,
    "Phone" text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT "Logoped_pkey" PRIMARY KEY ("Id")
)
--
--		3 Patient
--
CREATE TABLE IF NOT EXISTS logoped."Patient"
(
    "Id" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    "FirstName" text COLLATE pg_catalog."default",
    "LastName" text COLLATE pg_catalog."default",
    "DateOfBirth" timestamp without time zone,
    "UserId" uuid,
    "LogopedId" uuid,
    "IsHidden" boolean,
    CONSTRAINT "Patient_pkey" PRIMARY KEY ("Id"),
    CONSTRAINT "FK_patient_logoped" FOREIGN KEY ("LogopedId")
        REFERENCES logoped."Logoped" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT "FK_patient_user" FOREIGN KEY ("UserId")
        REFERENCES logoped."UserData" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
--
--		4 SpeechError
--
CREATE TABLE IF NOT EXISTS logoped."SpeechError"
(
    "Id" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    "Title" text COLLATE pg_catalog."default",
    "Description" text COLLATE pg_catalog."default",
    CONSTRAINT "SpeechError_pkey" PRIMARY KEY ("Id")
)
--
--		5 SoundCorrection
--
CREATE TABLE IF NOT EXISTS logoped."SoundCorrection"
(
    "Id" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    "Sound" text COLLATE pg_catalog."default",
    "Correction" text COLLATE pg_catalog."default",
    CONSTRAINT "SoundCorrection_pkey" PRIMARY KEY ("Id")
)
--
--		6 SpeechCard
--
CREATE TABLE IF NOT EXISTS logoped."SpeechCard"
(
    "Id" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    "Reason" text COLLATE pg_catalog."default",
    "StateOfHearning" text COLLATE pg_catalog."default",
    "Anamnesis" text COLLATE pg_catalog."default",
    "GeneralMotor" text COLLATE pg_catalog."default",
    "FineMotor" text COLLATE pg_catalog."default",
    "Articulatory" text COLLATE pg_catalog."default",
    "SoundReproduction" text COLLATE pg_catalog."default",
    "SoundComponition" text COLLATE pg_catalog."default",
    "SpeechChars" text COLLATE pg_catalog."default",
    "PatientChars" text COLLATE pg_catalog."default",
    CONSTRAINT "SpeechCard_pkey" PRIMARY KEY ("Id")
)
--
--		7 Homework
--
CREATE TABLE IF NOT EXISTS logoped."Homework"
(
    "Id" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    "Task" text COLLATE pg_catalog."default",
    CONSTRAINT "Homework_pkey" PRIMARY KEY ("Id")
)
--
--		8 Lesson
--
CREATE TABLE IF NOT EXISTS logoped."Lesson"
(
    "Id" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    "Type" text COLLATE pg_catalog."default",
    "Topic" text COLLATE pg_catalog."default",
    "Description" text COLLATE pg_catalog."default",
    "DateOfLesson" timestamp without time zone,
    "Status" text COLLATE pg_catalog."default",
    "HomeworkId" bigint,
    "LogopedId" uuid,
    CONSTRAINT "Lesson_pkey" PRIMARY KEY ("Id"),
    CONSTRAINT homework_uniq UNIQUE ("HomeworkId"),
    CONSTRAINT "FK_lesson_homework" FOREIGN KEY ("HomeworkId")
        REFERENCES logoped."Homework" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT "FK_lesson_logoped" FOREIGN KEY ("LogopedId")
        REFERENCES logoped."Logoped" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
--
--		9 Diagnostic
--
CREATE TABLE IF NOT EXISTS logoped."Diagnostic"
(
    "Id" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    "Date" timestamp without time zone,
    "LessonId" bigint,
    "SpeechCardId" bigint,
    CONSTRAINT "Diagnostic_pkey" PRIMARY KEY ("Id"),
    CONSTRAINT "speechCard_uniq" UNIQUE ("SpeechCardId"),
    CONSTRAINT "FK_diag_lesson" FOREIGN KEY ("LessonId")
        REFERENCES logoped."Lesson" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT "FK_diag_speechCard" FOREIGN KEY ("SpeechCardId")
        REFERENCES logoped."SpeechCard" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
--
--		10 SpeechCard_SoundCorrection
--
CREATE TABLE IF NOT EXISTS logoped."SpeechCard_SoundCorrection"
(
    "speechCardId" bigint NOT NULL,
    "speechCorrectionId" bigint NOT NULL,
    CONSTRAINT "SpeechCard_SoundCorrection_pkey" PRIMARY KEY ("speechCardId", "speechCorrectionId"),
    CONSTRAINT "FK_soundCorrection_scsc" FOREIGN KEY ("speechCorrectionId")
        REFERENCES logoped."SoundCorrection" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT "FK_speechCard_scsc" FOREIGN KEY ("speechCardId")
        REFERENCES logoped."SpeechCard" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
--
--		11 SpeechCard_SpeechError
--
CREATE TABLE IF NOT EXISTS logoped."SpeechCard_SpeechError"
(
    "speechCardId" bigint NOT NULL,
    "speechErrorId" bigint NOT NULL,
    CONSTRAINT "SpeechCard_SpeechError_pkey" PRIMARY KEY ("speechCardId", "speechErrorId"),
    CONSTRAINT "FK_speechCard_scse" FOREIGN KEY ("speechCardId")
        REFERENCES logoped."SpeechCard" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT "FK_speechError_scse" FOREIGN KEY ("speechErrorId")
        REFERENCES logoped."SpeechError" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
--
--		12 Lesson_Patient
--
CREATE TABLE IF NOT EXISTS logoped."Lesson_Patient"
(
    "LessonId" bigint NOT NULL,
    "PatientId" bigint NOT NULL,
    CONSTRAINT "LessonPatient_pkey" PRIMARY KEY ("LessonId", "PatientId"),
    CONSTRAINT "FK_lesson_lp" FOREIGN KEY ("LessonId")
        REFERENCES logoped."Lesson" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT "FK_patient_lp" FOREIGN KEY ("PatientId")
        REFERENCES logoped."Patient" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
-- у userdata, patient, logoped
--		.
--

