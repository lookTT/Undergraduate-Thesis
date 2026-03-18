CREATE DATABASE IF NOT EXISTS volunteer_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE volunteer_management;

CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    real_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    status TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    role_name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    create_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    UNIQUE KEY uk_user_role (user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS volunteer_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    student_or_member_no VARCHAR(50) NOT NULL,
    gender VARCHAR(10),
    age INT,
    community_name VARCHAR(100),
    skill_tag VARCHAR(255),
    remark VARCHAR(500),
    create_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_volunteer_profile_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    location VARCHAR(100) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    recruit_count INT NOT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    creator_id BIGINT NOT NULL,
    create_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_activity_creator FOREIGN KEY (creator_id) REFERENCES user(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS activity_signup (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_id BIGINT NOT NULL,
    volunteer_id BIGINT NOT NULL,
    apply_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    audit_status TINYINT NOT NULL DEFAULT 0,
    audit_time DATETIME NULL,
    audit_remark VARCHAR(255),
    create_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_activity_volunteer_signup (activity_id, volunteer_id),
    CONSTRAINT fk_signup_activity FOREIGN KEY (activity_id) REFERENCES activity(id) ON DELETE CASCADE,
    CONSTRAINT fk_signup_volunteer FOREIGN KEY (volunteer_id) REFERENCES volunteer_profile(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS activity_checkin (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_id BIGINT NOT NULL,
    volunteer_id BIGINT NOT NULL,
    checkin_time DATETIME NULL,
    checkin_status TINYINT NOT NULL DEFAULT 0,
    checkin_note VARCHAR(255),
    create_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_activity_volunteer_checkin (activity_id, volunteer_id),
    CONSTRAINT fk_checkin_activity FOREIGN KEY (activity_id) REFERENCES activity(id) ON DELETE CASCADE,
    CONSTRAINT fk_checkin_volunteer FOREIGN KEY (volunteer_id) REFERENCES volunteer_profile(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS service_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_id BIGINT NOT NULL,
    volunteer_id BIGINT NOT NULL,
    service_hours DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    record_source VARCHAR(50),
    create_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_activity_volunteer_record (activity_id, volunteer_id),
    CONSTRAINT fk_record_activity FOREIGN KEY (activity_id) REFERENCES activity(id) ON DELETE CASCADE,
    CONSTRAINT fk_record_volunteer FOREIGN KEY (volunteer_id) REFERENCES volunteer_profile(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
