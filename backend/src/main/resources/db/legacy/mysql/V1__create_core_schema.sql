-- MySQL 8.4; Flyway is responsible for applying this migration once.
CREATE TABLE users (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 email VARCHAR(254) NOT NULL UNIQUE,
 password_hash VARCHAR(255) NOT NULL,
 full_name VARCHAR(120) NOT NULL,
 phone VARCHAR(30),
 role VARCHAR(20) NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
 created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
 CONSTRAINT ck_user_role CHECK (role IN ('PARENT','TUTOR','ADMIN')),
 CONSTRAINT ck_user_status CHECK (status IN ('ACTIVE','LOCKED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_ci;

CREATE TABLE tutor_profiles (
 user_id BIGINT PRIMARY KEY,
 introduction TEXT,
 education VARCHAR(255),
 experience_years INT,
 supports_online BOOLEAN NOT NULL DEFAULT FALSE,
 supports_offline BOOLEAN NOT NULL DEFAULT FALSE,
 teaching_area VARCHAR(120),
 availability_note VARCHAR(500),
 status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
 version BIGINT NOT NULL DEFAULT 0,
 submitted_at DATETIME(6),
 updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
 CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES users(id),
 CONSTRAINT ck_profile_status CHECK (status IN ('DRAFT','PENDING_REVIEW','APPROVED','NEEDS_CHANGES')),
 CONSTRAINT ck_profile_experience CHECK (experience_years IS NULL OR experience_years >= 0),
 CONSTRAINT ck_profile_modes CHECK (supports_online IN (0,1) AND supports_offline IN (0,1)),
 CONSTRAINT ck_profile_ready CHECK (status NOT IN ('PENDING_REVIEW','APPROVED') OR
   ((supports_online = 1 OR supports_offline = 1) AND
    (supports_offline = 0 OR (teaching_area IS NOT NULL AND CHAR_LENGTH(TRIM(teaching_area)) > 0)))),
 INDEX idx_profile_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_ci;

CREATE TABLE subjects (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 name VARCHAR(100) NOT NULL UNIQUE,
 active BOOLEAN NOT NULL DEFAULT TRUE,
 CONSTRAINT ck_subject_active CHECK (active IN (0,1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_ci;

CREATE TABLE tutor_offerings (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 tutor_id BIGINT NOT NULL,
 subject_id BIGINT NOT NULL,
 grade_level INT NOT NULL,
 hourly_rate INT NOT NULL,
 CONSTRAINT fk_offering_tutor FOREIGN KEY (tutor_id) REFERENCES tutor_profiles(user_id),
 CONSTRAINT fk_offering_subject FOREIGN KEY (subject_id) REFERENCES subjects(id),
 CONSTRAINT uq_offering UNIQUE (tutor_id,subject_id,grade_level),
 CONSTRAINT ck_offering_grade CHECK (grade_level BETWEEN 1 AND 12),
 CONSTRAINT ck_offering_rate CHECK (hourly_rate BETWEEN 10000 AND 5000000 AND MOD(hourly_rate,1000)=0),
 INDEX idx_offering_search (subject_id,grade_level,hourly_rate)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_ci;

CREATE TABLE profile_reviews (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 tutor_id BIGINT NOT NULL,
 reviewer_id BIGINT NOT NULL,
 decision VARCHAR(30) NOT NULL,
 reason VARCHAR(1000),
 profile_snapshot JSON NOT NULL,
 reviewed_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 CONSTRAINT fk_profile_review_tutor FOREIGN KEY (tutor_id) REFERENCES tutor_profiles(user_id),
 CONSTRAINT fk_profile_review_admin FOREIGN KEY (reviewer_id) REFERENCES users(id),
 CONSTRAINT ck_profile_decision CHECK (decision IN ('APPROVED','NEEDS_CHANGES')),
 CONSTRAINT ck_profile_reason CHECK (decision <> 'NEEDS_CHANGES' OR (reason IS NOT NULL AND CHAR_LENGTH(TRIM(reason))>0)),
 INDEX idx_profile_review_history (tutor_id,reviewed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_ci;

CREATE TABLE tutoring_requests (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 parent_id BIGINT NOT NULL,
 tutor_id BIGINT NOT NULL,
 subject_id BIGINT NOT NULL,
 student_name VARCHAR(60) NOT NULL,
 student_name_normalized VARCHAR(60) NOT NULL,
 grade_level INT NOT NULL,
 learning_goal VARCHAR(500) NOT NULL,
 learning_mode VARCHAR(10) NOT NULL,
 learning_area VARCHAR(120),
 desired_schedule VARCHAR(200) NOT NULL,
 proposed_hourly_rate INT NOT NULL,
 desired_start_date DATE NOT NULL,
 note VARCHAR(500),
 status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
 rejection_reason VARCHAR(1000),
 version BIGINT NOT NULL DEFAULT 0,
 created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 resolved_at DATETIME(6),
 -- NULL permits multiple historical requests; 1 permits only one pending key.
 pending_marker TINYINT GENERATED ALWAYS AS (CASE WHEN status='PENDING' THEN 1 ELSE NULL END) STORED,
 CONSTRAINT fk_request_parent FOREIGN KEY (parent_id) REFERENCES users(id),
 CONSTRAINT fk_request_tutor FOREIGN KEY (tutor_id) REFERENCES tutor_profiles(user_id),
 CONSTRAINT fk_request_subject FOREIGN KEY (subject_id) REFERENCES subjects(id),
 CONSTRAINT uq_pending_request UNIQUE (parent_id,tutor_id,student_name_normalized,subject_id,pending_marker),
 CONSTRAINT ck_request_people CHECK (parent_id <> tutor_id),
 CONSTRAINT ck_request_grade CHECK (grade_level BETWEEN 1 AND 12),
 CONSTRAINT ck_request_mode CHECK (learning_mode IN ('ONLINE','OFFLINE')),
 CONSTRAINT ck_request_area CHECK (learning_mode='ONLINE' OR (learning_area IS NOT NULL AND CHAR_LENGTH(TRIM(learning_area))>0)),
 CONSTRAINT ck_request_text CHECK (CHAR_LENGTH(TRIM(student_name))>0 AND CHAR_LENGTH(TRIM(student_name_normalized))>0 AND CHAR_LENGTH(TRIM(learning_goal))>0 AND CHAR_LENGTH(TRIM(desired_schedule))>0),
 CONSTRAINT ck_request_rate CHECK (proposed_hourly_rate BETWEEN 10000 AND 5000000 AND MOD(proposed_hourly_rate,1000)=0),
 CONSTRAINT ck_request_status CHECK (status IN ('PENDING','ACCEPTED','REJECTED','CANCELLED')),
 CONSTRAINT ck_request_rejection CHECK (status <> 'REJECTED' OR (rejection_reason IS NOT NULL AND CHAR_LENGTH(TRIM(rejection_reason))>0)),
 CONSTRAINT ck_request_resolution CHECK ((status='PENDING' AND resolved_at IS NULL) OR (status<>'PENDING' AND resolved_at IS NOT NULL)),
 INDEX idx_request_parent (parent_id,status,created_at),
 INDEX idx_request_tutor (tutor_id,status,created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_ci;

CREATE TABLE tutoring_classes (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 request_id BIGINT NOT NULL UNIQUE,
 status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
 ended_by BIGINT,
 cancellation_reason VARCHAR(1000),
 version BIGINT NOT NULL DEFAULT 0,
 created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 ended_at DATETIME(6),
 CONSTRAINT fk_class_request FOREIGN KEY (request_id) REFERENCES tutoring_requests(id),
 CONSTRAINT fk_class_ended_by FOREIGN KEY (ended_by) REFERENCES users(id),
 CONSTRAINT ck_class_status CHECK (status IN ('ACTIVE','COMPLETED','CANCELLED')),
 CONSTRAINT ck_class_end CHECK ((status='ACTIVE' AND ended_at IS NULL AND ended_by IS NULL) OR (status<>'ACTIVE' AND ended_at IS NOT NULL AND ended_by IS NOT NULL)),
 CONSTRAINT ck_class_reason CHECK (status<>'CANCELLED' OR (cancellation_reason IS NOT NULL AND CHAR_LENGTH(TRIM(cancellation_reason))>0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_ci;

CREATE TABLE reviews (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 class_id BIGINT NOT NULL UNIQUE,
 rating INT NOT NULL,
 comment VARCHAR(1000),
 created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 CONSTRAINT fk_review_class FOREIGN KEY (class_id) REFERENCES tutoring_classes(id),
 CONSTRAINT ck_review_rating CHECK (rating BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_ci;
