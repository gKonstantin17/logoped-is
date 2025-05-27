--
--		1 USER
--
CREATE TABLE IF NOT EXISTS logoped."User_data"
(
    "Id" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    "FirstName" text COLLATE pg_catalog."default",
    "SecondName" text COLLATE pg_catalog."default",
    "Email" text COLLATE pg_catalog."default",
    "Phone" text COLLATE pg_catalog."default",
    CONSTRAINT "User_pkey" PRIMARY KEY ("Id")
)
--
--		2 Patient
--
CREATE TABLE IF NOT EXISTS logoped."Patient"
(
    "Id" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    "FirstName" text COLLATE pg_catalog."default",
    "SecondName" text COLLATE pg_catalog."default",
    "DateOfBirth" timestamp without time zone,
    "UserId" bigint NOT NULL,
    CONSTRAINT "Patient_pkey" PRIMARY KEY ("Id"),
    CONSTRAINT "FK_patient_user" FOREIGN KEY ("UserId")
        REFERENCES logoped."User_data" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
--
--		3 Logoped
--
CREATE TABLE IF NOT EXISTS logoped."Logoped"
(
    "Id" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    "FirstName" text COLLATE pg_catalog."default",
    "SecondName" text COLLATE pg_catalog."default",
    "Phone" text COLLATE pg_catalog."default",
    "Email" text COLLATE pg_catalog."default",
    CONSTRAINT "Logoped_pkey" PRIMARY KEY ("Id")
)
--
--		4 SpeechCard
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
--		5 SpeechError
--
CREATE TABLE IF NOT EXISTS logoped."SpeechError"
(
    "Id" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    "Title" text COLLATE pg_catalog."default",
    "Description" text COLLATE pg_catalog."default",
    CONSTRAINT "SpeechError_pkey" PRIMARY KEY ("Id")
)
--
--		6 SoundCorrection
--
CREATE TABLE IF NOT EXISTS logoped."SoundCorrection"
(
    "Id" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    "Sound" text COLLATE pg_catalog."default",
    "Correction" text COLLATE pg_catalog."default",
    CONSTRAINT "SoundCorrection_pkey" PRIMARY KEY ("Id")
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
--		8 Diagnostic
--
CREATE TABLE IF NOT EXISTS logoped."Diagnostic"
(
    "Id" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    "Date" timestamp without time zone,
    "patientId" bigint NOT NULL,
    "SpeechCardId" bigint NOT NULL,
    "LogopedId" bigint NOT NULL,
    CONSTRAINT "Diagnostic_pkey" PRIMARY KEY ("Id"),
    CONSTRAINT "speechCard_uniq" UNIQUE ("SpeechCardId"),
    CONSTRAINT "FK_diag_logoped" FOREIGN KEY ("LogopedId")
        REFERENCES logoped."Logoped" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT "FK_diag_patient" FOREIGN KEY ("patientId")
        REFERENCES logoped."Patient" ("Id") MATCH SIMPLE
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
--		9 Lesson
--
CREATE TABLE IF NOT EXISTS logoped."Lesson"
(
    "Id" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    "Type" text COLLATE pg_catalog."default",
    "Description" text COLLATE pg_catalog."default",
    "DateOfLesson" timestamp without time zone,
    "Duraction" integer,
    "diagnosticId" bigint,
    "homeworkId" bigint,
    "logopedId" bigint NOT NULL,
    CONSTRAINT "Lesson_pkey" PRIMARY KEY ("Id"),
    CONSTRAINT diag_uniq UNIQUE ("diagnosticId"),
    CONSTRAINT "FK_lesson_diag" FOREIGN KEY ("diagnosticId")
        REFERENCES logoped."Diagnostic" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT "FK_lesson_homework" FOREIGN KEY ("homeworkId")
        REFERENCES logoped."Homework" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT "FK_lesson_logoped" FOREIGN KEY ("logopedId")
        REFERENCES logoped."Logoped" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
--
--		10 Logoped_SpeechError
--
CREATE TABLE IF NOT EXISTS logoped."Logoped_SpeechError"
(
    "logopedId" bigint NOT NULL,
    "speechErrorId" bigint NOT NULL,
    CONSTRAINT "Logoped_SpeechError_pkey" PRIMARY KEY ("logopedId", "speechErrorId"),
    CONSTRAINT "FK_speechError_lse" FOREIGN KEY ("speechErrorId")
        REFERENCES logoped."SpeechError" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT "FL_logoped_lse" FOREIGN KEY ("logopedId")
        REFERENCES logoped."Logoped" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
--
--		11 SpeechCard_SoundCorrection
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
--		12 SpeechCard_SpeechError
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
--		13 Lesson_Patient
--
CREATE TABLE IF NOT EXISTS logoped."Lesson_Patient"
(
    "lessonId" bigint NOT NULL,
    "patientId" bigint NOT NULL,
    CONSTRAINT "LessonPatient_pkey" PRIMARY KEY ("lessonId", "patientId"),
    CONSTRAINT "FK_lesson_lp" FOREIGN KEY ("lessonId")
        REFERENCES logoped."Lesson" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT "FK_patient_lp" FOREIGN KEY ("patientId")
        REFERENCES logoped."Patient" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
--
--		14 Lesson_SoundCorrection
--
CREATE TABLE IF NOT EXISTS logoped."Lesson_SoundCorrection"
(
    "lessonId" bigint NOT NULL,
    "soundCorrectionId" bigint NOT NULL,
    CONSTRAINT "Lesson_SoundCorrection_pkey" PRIMARY KEY ("lessonId", "soundCorrectionId"),
    CONSTRAINT "FK_lesson_lsc" FOREIGN KEY ("lessonId")
        REFERENCES logoped."Lesson" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT "FK_soundCorrection_lsc" FOREIGN KEY ("soundCorrectionId")
        REFERENCES logoped."SoundCorrection" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
--
--		15 Lesson_SpeechError
--
CREATE TABLE IF NOT EXISTS logoped."Lesson_SpeechError"
(
    "lessonId" bigint NOT NULL,
    "speechErrorId" bigint NOT NULL,
    CONSTRAINT "Lesson_SpeechError_pkey" PRIMARY KEY ("lessonId", "speechErrorId"),
    CONSTRAINT "FK_lesson_lse" FOREIGN KEY ("lessonId")
        REFERENCES logoped."Lesson" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT "FK_speechError_lse" FOREIGN KEY ("speechErrorId")
        REFERENCES logoped."SpeechError" ("Id") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
--
--		.
--

