-- Optional reference if you use PostgreSQL. The school demo runs with H2 in memory (see application.properties).

CREATE TABLE IF NOT EXISTS hotel (
    id BIGSERIAL PRIMARY KEY,
    hotel_name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    phone_number VARCHAR(64),
    email VARCHAR(255),
    description TEXT
);

CREATE TABLE IF NOT EXISTS guest (
    id BIGSERIAL PRIMARY KEY,
    guest_id VARCHAR(64) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(64),
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS room_type (
    id BIGSERIAL PRIMARY KEY,
    type_name VARCHAR(100) NOT NULL,
    room_price NUMERIC(12, 2) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS room (
    id BIGSERIAL PRIMARY KEY,
    room_number VARCHAR(32) UNIQUE NOT NULL,
    room_type_id BIGINT NOT NULL REFERENCES room_type (id),
    price_per_night NUMERIC(12, 2) NOT NULL,
    availability_status BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS offered_service (
    id BIGSERIAL PRIMARY KEY,
    service_code VARCHAR(32) UNIQUE NOT NULL,
    service_name VARCHAR(255) NOT NULL,
    service_price NUMERIC(12, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS reservation (
    id BIGSERIAL PRIMARY KEY,
    reservation_id VARCHAR(64) UNIQUE NOT NULL,
    guest_id BIGINT NOT NULL REFERENCES guest (id),
    check_in_date DATE NOT NULL,
    number_of_nights INT NOT NULL,
    reservation_status VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS reservation_room (
    reservation_id BIGINT NOT NULL REFERENCES reservation (id) ON DELETE CASCADE,
    room_id BIGINT NOT NULL REFERENCES room (id),
    PRIMARY KEY (reservation_id, room_id)
);

CREATE TABLE IF NOT EXISTS reservation_service (
    reservation_id BIGINT NOT NULL REFERENCES reservation (id) ON DELETE CASCADE,
    service_id BIGINT NOT NULL REFERENCES offered_service (id),
    PRIMARY KEY (reservation_id, service_id)
);

CREATE TABLE IF NOT EXISTS invoice (
    id BIGSERIAL PRIMARY KEY,
    reservation_id BIGINT UNIQUE NOT NULL REFERENCES reservation (id),
    total_room_cost NUMERIC(12, 2) NOT NULL,
    total_service_cost NUMERIC(12, 2) NOT NULL,
    total_amount NUMERIC(12, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS payment (
    id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL REFERENCES invoice (id),
    payment_method VARCHAR(64) NOT NULL,
    amount_paid NUMERIC(12, 2) NOT NULL,
    payment_status VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS cancellation (
    id BIGSERIAL PRIMARY KEY,
    reservation_id BIGINT UNIQUE NOT NULL REFERENCES reservation (id),
    cancellation_reason TEXT,
    cancellation_fee NUMERIC(12, 2) NOT NULL,
    refund_amount NUMERIC(12, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS review (
    id BIGSERIAL PRIMARY KEY,
    guest_id BIGINT REFERENCES guest (id),
    guest_name VARCHAR(255),
    rating INT NOT NULL,
    comment TEXT
);
