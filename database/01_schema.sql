-- ==============================================================================
-- IRCTC Railway Reservation System - Database Schema DDL
-- Target DBMS: MySQL 8.0+
-- Storage Engine: InnoDB
-- Character Set: utf8mb4 / Collation: utf8mb4_unicode_ci
-- ==============================================================================

-- 1. Database Creation (if not already existing)
CREATE DATABASE IF NOT EXISTS `irctc_db`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `irctc_db`;

-- ==============================================================================
-- 2. Drop existing tables in reverse-dependency order (safe re-execution)
-- ==============================================================================
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `cancellations`;
DROP TABLE IF EXISTS `payments`;
DROP TABLE IF EXISTS `booking_passengers`;
DROP TABLE IF EXISTS `bookings`;
DROP TABLE IF EXISTS `train_schedules`;
DROP TABLE IF EXISTS `seats`;
DROP TABLE IF EXISTS `coaches`;
DROP TABLE IF EXISTS `route_stops`;
DROP TABLE IF EXISTS `trains`;
DROP TABLE IF EXISTS `stations`;
DROP TABLE IF EXISTS `users`;

SET FOREIGN_KEY_CHECKS = 1;

-- ==============================================================================
-- 3. Core Tables
-- ==============================================================================

-- ------------------------------------------------------------------------------
-- Table: users
-- Stores registered passenger accounts and administrative credentials
-- ------------------------------------------------------------------------------
CREATE TABLE `users` (
    `user_id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL,
    `password_hash` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(15) NOT NULL,
    `role` ENUM('PASSENGER', 'ADMIN') NOT NULL DEFAULT 'PASSENGER',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`),
    CONSTRAINT `uq_users_username` UNIQUE (`username`),
    CONSTRAINT `uq_users_email` UNIQUE (`email`),
    CONSTRAINT `uq_users_phone` UNIQUE (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- Table: stations
-- Master registry of railway network stations
-- ------------------------------------------------------------------------------
CREATE TABLE `stations` (
    `station_id` INT NOT NULL AUTO_INCREMENT,
    `station_code` VARCHAR(10) NOT NULL,
    `station_name` VARCHAR(100) NOT NULL,
    `city` VARCHAR(50) NOT NULL,
    `state` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`station_id`),
    CONSTRAINT `uq_stations_code` UNIQUE (`station_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- Table: trains
-- Master registry of train services and endpoints
-- ------------------------------------------------------------------------------
CREATE TABLE `trains` (
    `train_id` INT NOT NULL AUTO_INCREMENT,
    `train_number` VARCHAR(10) NOT NULL,
    `train_name` VARCHAR(100) NOT NULL,
    `train_type` ENUM('RAJDHANI', 'SHATABDI', 'SUPERFAST', 'EXPRESS', 'MAIL') NOT NULL,
    `source_station_id` INT NOT NULL,
    `destination_station_id` INT NOT NULL,
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (`train_id`),
    CONSTRAINT `uq_trains_number` UNIQUE (`train_number`),
    CONSTRAINT `fk_trains_source` FOREIGN KEY (`source_station_id`)
        REFERENCES `stations` (`station_id`)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_trains_dest` FOREIGN KEY (`destination_station_id`)
        REFERENCES `stations` (`station_id`)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `chk_trains_distinct_stations` CHECK (`source_station_id` <> `destination_station_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- Table: route_stops
-- Defines ordered intermediate halting stations, timings, and cumulative distance
-- ------------------------------------------------------------------------------
CREATE TABLE `route_stops` (
    `route_stop_id` INT NOT NULL AUTO_INCREMENT,
    `train_id` INT NOT NULL,
    `station_id` INT NOT NULL,
    `stop_sequence` INT NOT NULL,
    `arrival_time` TIME NULL,
    `departure_time` TIME NULL,
    `distance_from_source_km` INT NOT NULL DEFAULT 0,
    `day_offset` TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`route_stop_id`),
    CONSTRAINT `fk_route_stops_train` FOREIGN KEY (`train_id`)
        REFERENCES `trains` (`train_id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_route_stops_station` FOREIGN KEY (`station_id`)
        REFERENCES `stations` (`station_id`)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `uq_train_stop_sequence` UNIQUE (`train_id`, `stop_sequence`),
    CONSTRAINT `uq_train_station` UNIQUE (`train_id`, `station_id`),
    CONSTRAINT `chk_stop_sequence` CHECK (`stop_sequence` >= 1),
    CONSTRAINT `chk_distance` CHECK (`distance_from_source_km` >= 0),
    CONSTRAINT `chk_day_offset` CHECK (`day_offset` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- Table: coaches
-- Defines coach composition and travel service classes per train
-- ------------------------------------------------------------------------------
CREATE TABLE `coaches` (
    `coach_id` INT NOT NULL AUTO_INCREMENT,
    `train_id` INT NOT NULL,
    `coach_label` VARCHAR(10) NOT NULL,
    `travel_class` ENUM('1A', '2A', '3A', 'SL', '2S', 'CC') NOT NULL,
    `total_seats` INT NOT NULL,
    PRIMARY KEY (`coach_id`),
    CONSTRAINT `fk_coaches_train` FOREIGN KEY (`train_id`)
        REFERENCES `trains` (`train_id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `uq_train_coach` UNIQUE (`train_id`, `coach_label`),
    CONSTRAINT `chk_total_seats` CHECK (`total_seats` > 0 AND `total_seats` <= 120)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- Table: seats
-- Physical individual seats mapped to coach layouts
-- ------------------------------------------------------------------------------
CREATE TABLE `seats` (
    `seat_id` INT NOT NULL AUTO_INCREMENT,
    `coach_id` INT NOT NULL,
    `seat_number` INT NOT NULL,
    `berth_type` ENUM('LOWER', 'MIDDLE', 'UPPER', 'SIDE_LOWER', 'SIDE_UPPER', 'CABIN', 'WINDOW', 'AISLE') NOT NULL,
    PRIMARY KEY (`seat_id`),
    CONSTRAINT `fk_seats_coach` FOREIGN KEY (`coach_id`)
        REFERENCES `coaches` (`coach_id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `uq_coach_seat_number` UNIQUE (`coach_id`, `seat_number`),
    CONSTRAINT `chk_seat_number` CHECK (`seat_number` >= 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- Table: train_schedules
-- Calendar-dated operating runs for trains
-- ------------------------------------------------------------------------------
CREATE TABLE `train_schedules` (
    `schedule_id` BIGINT NOT NULL AUTO_INCREMENT,
    `train_id` INT NOT NULL,
    `journey_date` DATE NOT NULL,
    `status` ENUM('SCHEDULED', 'RUNNING', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'SCHEDULED',
    PRIMARY KEY (`schedule_id`),
    CONSTRAINT `fk_schedules_train` FOREIGN KEY (`train_id`)
        REFERENCES `trains` (`train_id`)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `uq_train_journey_date` UNIQUE (`train_id`, `journey_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- Table: bookings
-- Master reservation entity tracking booking order and unique PNR
-- ------------------------------------------------------------------------------
CREATE TABLE `bookings` (
    `booking_id` BIGINT NOT NULL AUTO_INCREMENT,
    `pnr_number` VARCHAR(10) NOT NULL,
    `user_id` BIGINT NOT NULL,
    `schedule_id` BIGINT NOT NULL,
    `from_station_id` INT NOT NULL,
    `to_station_id` INT NOT NULL,
    `travel_class` ENUM('1A', '2A', '3A', 'SL', '2S', 'CC') NOT NULL,
    `booking_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `total_fare` DECIMAL(10,2) NOT NULL,
    `booking_status` ENUM('CONFIRMED', 'WAITLISTED', 'CANCELLED', 'PARTIALLY_CANCELLED') NOT NULL DEFAULT 'CONFIRMED',
    PRIMARY KEY (`booking_id`),
    CONSTRAINT `uq_bookings_pnr` UNIQUE (`pnr_number`),
    CONSTRAINT `fk_bookings_user` FOREIGN KEY (`user_id`)
        REFERENCES `users` (`user_id`)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_bookings_schedule` FOREIGN KEY (`schedule_id`)
        REFERENCES `train_schedules` (`schedule_id`)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_bookings_from_station` FOREIGN KEY (`from_station_id`)
        REFERENCES `stations` (`station_id`)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_bookings_to_station` FOREIGN KEY (`to_station_id`)
        REFERENCES `stations` (`station_id`)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `chk_bookings_distinct_stations` CHECK (`from_station_id` <> `to_station_id`),
    CONSTRAINT `chk_bookings_total_fare` CHECK (`total_fare` >= 0.00)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- Table: booking_passengers
-- Individual traveler records linked to a booking (1 to 6 per PNR)
-- ------------------------------------------------------------------------------
CREATE TABLE `booking_passengers` (
    `passenger_id` BIGINT NOT NULL AUTO_INCREMENT,
    `booking_id` BIGINT NOT NULL,
    `full_name` VARCHAR(100) NOT NULL,
    `age` INT NOT NULL,
    `gender` ENUM('MALE', 'FEMALE', 'TRANSGENDER') NOT NULL,
    `berth_preference` ENUM('NO_PREFERENCE', 'LOWER', 'MIDDLE', 'UPPER', 'SIDE_LOWER', 'SIDE_UPPER') NOT NULL DEFAULT 'NO_PREFERENCE',
    `seat_id` INT NULL,
    `passenger_status` ENUM('CONFIRMED', 'RAC', 'WAITLISTED', 'CANCELLED') NOT NULL,
    `fare` DECIMAL(8,2) NOT NULL,
    PRIMARY KEY (`passenger_id`),
    CONSTRAINT `fk_passengers_booking` FOREIGN KEY (`booking_id`)
        REFERENCES `bookings` (`booking_id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_passengers_seat` FOREIGN KEY (`seat_id`)
        REFERENCES `seats` (`seat_id`)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `chk_passenger_age` CHECK (`age` > 0 AND `age` <= 120),
    CONSTRAINT `chk_passenger_fare` CHECK (`fare` >= 0.00)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- Table: payments
-- Simulated payment gateway transaction ledger
-- ------------------------------------------------------------------------------
CREATE TABLE `payments` (
    `payment_id` BIGINT NOT NULL AUTO_INCREMENT,
    `booking_id` BIGINT NOT NULL,
    `transaction_reference` VARCHAR(64) NOT NULL,
    `payment_method` ENUM('UPI', 'DEBIT_CARD', 'CREDIT_CARD', 'NET_BANKING', 'WALLET') NOT NULL,
    `amount` DECIMAL(10,2) NOT NULL,
    `payment_status` ENUM('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    `payment_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`payment_id`),
    CONSTRAINT `uq_payments_txn` UNIQUE (`transaction_reference`),
    CONSTRAINT `fk_payments_booking` FOREIGN KEY (`booking_id`)
        REFERENCES `bookings` (`booking_id`)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `chk_payment_amount` CHECK (`amount` > 0.00)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- Table: cancellations
-- Audit ledger for full or partial ticket cancellations and refunds
-- ------------------------------------------------------------------------------
CREATE TABLE `cancellations` (
    `cancellation_id` BIGINT NOT NULL AUTO_INCREMENT,
    `booking_id` BIGINT NOT NULL,
    `passenger_id` BIGINT NULL,
    `cancellation_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `refund_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `cancellation_charge` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    `cancellation_reason` VARCHAR(255) NULL,
    PRIMARY KEY (`cancellation_id`),
    CONSTRAINT `fk_cancellations_booking` FOREIGN KEY (`booking_id`)
        REFERENCES `bookings` (`booking_id`)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_cancellations_passenger` FOREIGN KEY (`passenger_id`)
        REFERENCES `booking_passengers` (`passenger_id`)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `chk_cancellation_refund` CHECK (`refund_amount` >= 0.00),
    CONSTRAINT `chk_cancellation_charge` CHECK (`cancellation_charge` >= 0.00)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 4. Performance Indexes
-- ==============================================================================

-- Search trains by halt sequence and stations
CREATE INDEX `idx_route_stops_search`
    ON `route_stops` (`station_id`, `train_id`, `stop_sequence`);

-- Search train runs by date
CREATE INDEX `idx_train_schedules_search`
    ON `train_schedules` (`train_id`, `journey_date`);

-- Search coaches by train and class
CREATE INDEX `idx_coaches_train_class`
    ON `coaches` (`train_id`, `travel_class`);

-- PNR status lookup (frequently queried)
CREATE INDEX `idx_bookings_pnr`
    ON `bookings` (`pnr_number`);

-- Passenger booking history for a user
CREATE INDEX `idx_bookings_user_date`
    ON `bookings` (`user_id`, `booking_date`);

-- Seat occupancy check for a schedule
CREATE INDEX `idx_passengers_seat_status`
    ON `booking_passengers` (`seat_id`, `passenger_status`);

-- Payments lookup by booking
CREATE INDEX `idx_payments_booking`
    ON `payments` (`booking_id`);
