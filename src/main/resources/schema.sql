-- -- 检查并创建用户
-- DO $$
-- BEGIN
--   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'old_school_user') THEN
--     CREATE USER old_school_user WITH PASSWORD 'zssg0914';
--   END IF;
-- END
-- $$;

-- -- 检查并创建数据库
-- DO $$
-- BEGIN
--   IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'old_school') THEN
--     CREATE DATABASE old_school
--     WITH
--     OWNER = old_school_user
--     ENCODING = 'UTF8'
--     LOCALE_PROVIDER = 'libc'
--     CONNECTION LIMIT = -1
--     IS_TEMPLATE = False;
--   END IF;
-- END
-- $$;


CREATE TABLE IF NOT EXISTS public.service_site (
    id BIGSERIAL PRIMARY KEY,
    site_name VARCHAR(100) NOT NULL,
    district VARCHAR(50) NOT NULL,
    address VARCHAR(200) NOT NULL,
    contact_phone VARCHAR(100) NOT NULL,
    qr_code_path VARCHAR(1024),
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    opening_hours VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    audit_status INTEGER NOT NULL DEFAULT 0,
    audit_remark VARCHAR(500),
    auditor_id BIGINT,
    auditor_name VARCHAR(50),
    audit_time TIMESTAMP,
    CONSTRAINT latitude_check CHECK (latitude >= -90 AND latitude <= 90),
    CONSTRAINT longitude_check CHECK (longitude >= -180 AND longitude <= 180)
);

CREATE TABLE IF NOT EXISTS public.site_image (
    id BIGSERIAL PRIMARY KEY,
    service_site_id BIGINT NOT NULL,
    image_path VARCHAR(1024) NOT NULL,
    image_order INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (service_site_id) REFERENCES public.service_site(id) ON DELETE CASCADE
);

-- 教师表
CREATE TABLE IF NOT EXISTS public.teacher (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    id_card VARCHAR(18) NOT NULL UNIQUE,
    contact_phone VARCHAR(11) NOT NULL,
    age INTEGER NOT NULL,
    gender VARCHAR(2) NOT NULL,
    course_types VARCHAR(255) NOT NULL, -- 存储课程类型的枚举名称，用逗号分隔
    introduction TEXT,
    photo_path VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 课程表
CREATE TABLE IF NOT EXISTS public.course (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    teacher_id BIGINT NOT NULL,
    service_site_id BIGINT NOT NULL,
    course_type VARCHAR(50) NOT NULL,
    course_subtype VARCHAR(50),
    is_charged BOOLEAN DEFAULT false,
    fee DECIMAL(10,2),
    year INTEGER NOT NULL,
    semester VARCHAR(20) NOT NULL,
    enrollment_start_date DATE NOT NULL,
    enrollment_end_date DATE NOT NULL,
    course_start_date DATE NOT NULL,
    course_end_date DATE NOT NULL,
    max_students INTEGER NOT NULL,
    location VARCHAR(200) NOT NULL,
    description TEXT,
    cover_image VARCHAR(255),
    course_status VARCHAR(20) DEFAULT 'DRAFT',
    total_hours INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (teacher_id) REFERENCES public.teacher(id),
    FOREIGN KEY (service_site_id) REFERENCES public.service_site(id)
);

-- 课程学时表
CREATE TABLE IF NOT EXISTS public.course_schedule (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    sub_course_name VARCHAR(100) NOT NULL,
    hours INTEGER NOT NULL,
    class_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (course_id) REFERENCES public.course(id)
);

CREATE TABLE IF NOT EXISTS public.course_reservation (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    student_name VARCHAR(50) NOT NULL,
    id_card VARCHAR(18) NOT NULL,
    phone_number VARCHAR(11) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (course_id) REFERENCES public.course(id)
);

-- 课程签到记录表
CREATE TABLE IF NOT EXISTS public.course_attendance (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    schedule_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    attendance_status VARCHAR(20) NOT NULL DEFAULT 'ABSENT',
    operator_id BIGINT,
    operator_name VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (course_id) REFERENCES public.course(id),
    FOREIGN KEY (schedule_id) REFERENCES public.course_schedule(id),
    FOREIGN KEY (student_id) REFERENCES public.course_reservation(id)
);

-- 为提高查询效率，添加索引
CREATE INDEX IF NOT EXISTS idx_course_attendance_course_id ON public.course_attendance(course_id);
CREATE INDEX IF NOT EXISTS idx_course_attendance_schedule_id ON public.course_attendance(schedule_id);
CREATE INDEX IF NOT EXISTS idx_course_attendance_student_id ON public.course_attendance(student_id);

CREATE TABLE IF NOT EXISTS public.role (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    sort INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS public.user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    role_id BIGINT NOT NULL,
    district VARCHAR(50) NOT NULL,
    service_site_id BIGINT NOT NULL,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (service_site_id) REFERENCES public.service_site(id),
    FOREIGN KEY (role_id) REFERENCES public.role(id)
);

CREATE TABLE IF NOT EXISTS public.favorite (
    id BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL,
    target_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_favorite_user_name ON public.favorite(user_name);
CREATE INDEX IF NOT EXISTS idx_favorite_target_id ON public.favorite(target_id);



