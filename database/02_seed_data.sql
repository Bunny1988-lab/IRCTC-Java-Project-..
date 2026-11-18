-- ==============================================================================
-- IRCTC Railway Reservation System - Seed Data
-- Target DBMS: MySQL 8.0+
-- Prerequisites: Execute 01_schema.sql before running this script
-- ==============================================================================

USE `irctc_db`;

-- Temporarily disable foreign key checks for clean truncation and insertion
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE `cancellations`;
TRUNCATE TABLE `payments`;
TRUNCATE TABLE `booking_passengers`;
TRUNCATE TABLE `bookings`;
TRUNCATE TABLE `train_schedules`;
TRUNCATE TABLE `seats`;
TRUNCATE TABLE `coaches`;
TRUNCATE TABLE `route_stops`;
TRUNCATE TABLE `trains`;
TRUNCATE TABLE `stations`;
TRUNCATE TABLE `users`;

SET FOREIGN_KEY_CHECKS = 1;

-- ==============================================================================
-- 1. Seed Users
-- Passwords are representative test hashes (SHA-256 for testing simplicity)
-- e.g., 'Admin@123' / 'User@123'
-- ==============================================================================
INSERT INTO `users` (`user_id`, `username`, `password_hash`, `full_name`, `email`, `phone`, `role`) VALUES
(1, 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'System Administrator', 'admin@irctc.res.in', '9999900001', 'ADMIN'),
(2, 'rahul_sharma', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'Rahul Sharma', 'rahul.sharma@example.com', '9811122233', 'PASSENGER'),
(3, 'priya_patel', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'Priya Patel', 'priya.patel@example.com', '9822233344', 'PASSENGER'),
(4, 'amit_verma', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'Amit Verma', 'amit.verma@example.com', '9833344455', 'PASSENGER');

-- ==============================================================================
-- 2. Seed Stations
-- Major Indian Railways Network Hubs
-- ==============================================================================
INSERT INTO `stations` (`station_id`, `station_code`, `station_name`, `city`, `state`) VALUES
(1, 'NDLS', 'New Delhi Railway Station', 'New Delhi', 'Delhi'),
(2, 'MMCT', 'Mumbai Central', 'Mumbai', 'Maharashtra'),
(3, 'BVI',  'Borivali', 'Mumbai', 'Maharashtra'),
(4, 'ST',   'Surat', 'Surat', 'Gujarat'),
(5, 'BRC',  'Vadodara Junction', 'Vadodara', 'Gujarat'),
(6, 'RTM',  'Ratlam Junction', 'Ratlam', 'Madhya Pradesh'),
(7, 'KOTA', 'Kota Junction', 'Kota', 'Rajasthan'),
(8, 'CNB',  'Kanpur Central', 'Kanpur', 'Uttar Pradesh'),
(9, 'HWH',  'Howrah Junction', 'Kolkata', 'West Bengal'),
(10, 'MAS', 'Chennai Central', 'Chennai', 'Tamil Nadu');

-- ==============================================================================
-- 3. Seed Trains
-- Iconic Premier Express & Rajdhani Services
-- ==============================================================================
INSERT INTO `trains` (`train_id`, `train_number`, `train_name`, `train_type`, `source_station_id`, `destination_station_id`, `is_active`) VALUES
(1, '12951', 'Mumbai Rajdhani Express', 'RAJDHANI', 2, 1, TRUE),   -- MMCT -> NDLS
(2, '12952', 'New Delhi Rajdhani Express', 'RAJDHANI', 1, 2, TRUE),  -- NDLS -> MMCT
(3, '12002', 'Bhopal Shatabdi Express', 'SHATABDI', 1, 8, TRUE),    -- NDLS -> CNB
(4, '12301', 'Howrah Rajdhani Express', 'RAJDHANI', 9, 1, TRUE);    -- HWH -> NDLS

-- ==============================================================================
-- 4. Seed Route Stops
-- Ordered route sequence with cumulative distance and halts
-- ==============================================================================

-- Train 12951 (MMCT to NDLS)
INSERT INTO `route_stops` (`train_id`, `station_id`, `stop_sequence`, `arrival_time`, `departure_time`, `distance_from_source_km`, `day_offset`) VALUES
(1, 2, 1, NULL,       '17:00:00', 0,    0), -- MMCT (Source)
(1, 3, 2, '17:22:00', '17:24:00', 30,   0), -- BVI
(1, 4, 3, '19:43:00', '19:48:00', 263,  0), -- ST
(1, 5, 4, '21:06:00', '21:16:00', 392,  0), -- BRC
(1, 6, 5, '00:25:00', '00:28:00', 653,  1), -- RTM
(1, 7, 6, '03:15:00', '03:20:00', 920,  1), -- KOTA
(1, 1, 7, '08:32:00', NULL,       1386, 1); -- NDLS (Destination)

-- Train 12952 (NDLS to MMCT)
INSERT INTO `route_stops` (`train_id`, `station_id`, `stop_sequence`, `arrival_time`, `departure_time`, `distance_from_source_km`, `day_offset`) VALUES
(2, 1, 1, NULL,       '16:55:00', 0,    0), -- NDLS (Source)
(2, 7, 2, '21:30:00', '21:40:00', 466,  0), -- KOTA
(2, 6, 3, '00:35:00', '00:38:00', 733,  1), -- RTM
(2, 5, 4, '03:40:00', '03:50:00', 994,  1), -- BRC
(2, 4, 5, '05:13:00', '05:18:00', 1123, 1), -- ST
(2, 3, 6, '07:40:00', '07:42:00', 1356, 1), -- BVI
(2, 2, 7, '08:35:00', NULL,       1386, 1); -- MMCT (Destination)

-- ==============================================================================
-- 5. Seed Coaches
-- Physical Coach configuration for Train 12951 & 12952
-- ==============================================================================
INSERT INTO `coaches` (`coach_id`, `train_id`, `coach_label`, `travel_class`, `total_seats`) VALUES
(1, 1, 'H1', '1A', 8),   -- Train 1: First AC
(2, 1, 'A1', '2A', 12),  -- Train 1: Second AC
(3, 1, 'B1', '3A', 16),  -- Train 1: Third AC
(4, 1, 'B2', '3A', 16),  -- Train 1: Third AC
(5, 2, 'A1', '2A', 12),  -- Train 2: Second AC
(6, 2, 'B1', '3A', 16);  -- Train 2: Third AC

-- ==============================================================================
-- 6. Seed Seats
-- Physical Seat mapping with berth layout
-- ==============================================================================

-- Seats for Coach H1 (Train 1 - 1A)
INSERT INTO `seats` (`coach_id`, `seat_number`, `berth_type`) VALUES
(1, 1, 'CABIN'), (1, 2, 'CABIN'), (1, 3, 'CABIN'), (1, 4, 'CABIN'),
(1, 5, 'CABIN'), (1, 6, 'CABIN'), (1, 7, 'CABIN'), (1, 8, 'CABIN');

-- Seats for Coach A1 (Train 1 - 2A)
INSERT INTO `seats` (`coach_id`, `seat_number`, `berth_type`) VALUES
(2, 1, 'LOWER'), (2, 2, 'UPPER'), (2, 3, 'SIDE_LOWER'), (2, 4, 'SIDE_UPPER'),
(2, 5, 'LOWER'), (2, 6, 'UPPER'), (2, 7, 'SIDE_LOWER'), (2, 8, 'SIDE_UPPER'),
(2, 9, 'LOWER'), (2, 10, 'UPPER'), (2, 11, 'SIDE_LOWER'), (2, 12, 'SIDE_UPPER');

-- Seats for Coach B1 (Train 1 - 3A)
INSERT INTO `seats` (`coach_id`, `seat_number`, `berth_type`) VALUES
(3, 1, 'LOWER'), (3, 2, 'MIDDLE'), (3, 3, 'UPPER'),
(3, 4, 'LOWER'), (3, 5, 'MIDDLE'), (3, 6, 'UPPER'),
(3, 7, 'SIDE_LOWER'), (3, 8, 'SIDE_UPPER'),
(3, 9, 'LOWER'), (3, 10, 'MIDDLE'), (3, 11, 'UPPER'),
(3, 12, 'LOWER'), (3, 13, 'MIDDLE'), (3, 14, 'UPPER'),
(3, 15, 'SIDE_LOWER'), (3, 16, 'SIDE_UPPER');

-- Seats for Coach B2 (Train 1 - 3A)
INSERT INTO `seats` (`coach_id`, `seat_number`, `berth_type`) VALUES
(4, 1, 'LOWER'), (4, 2, 'MIDDLE'), (4, 3, 'UPPER'),
(4, 4, 'LOWER'), (4, 5, 'MIDDLE'), (4, 6, 'UPPER'),
(4, 7, 'SIDE_LOWER'), (4, 8, 'SIDE_UPPER'),
(4, 9, 'LOWER'), (4, 10, 'MIDDLE'), (4, 11, 'UPPER'),
(4, 12, 'LOWER'), (4, 13, 'MIDDLE'), (4, 14, 'UPPER'),
(4, 15, 'SIDE_LOWER'), (4, 16, 'SIDE_UPPER');

-- ==============================================================================
-- 7. Seed Train Schedules
-- Scheduled operational runs
-- ==============================================================================
INSERT INTO `train_schedules` (`schedule_id`, `train_id`, `journey_date`, `status`) VALUES
(1, 1, '2026-12-01', 'SCHEDULED'),
(2, 1, '2026-12-02', 'SCHEDULED'),
(3, 1, '2026-12-03', 'SCHEDULED'),
(4, 2, '2026-12-01', 'SCHEDULED'),
(5, 2, '2026-12-02', 'SCHEDULED');

-- ==============================================================================
-- 8. Seed Sample Historical Booking & Payment
-- Provides working test records for Immediate Read/Status validation
-- ==============================================================================

-- Booking 1: PNR '8245192837'
-- Booked by Rahul Sharma on Train 12951 (Schedule 1: 2026-12-01) from MMCT to NDLS in 3A
INSERT INTO `bookings` (`booking_id`, `pnr_number`, `user_id`, `schedule_id`, `from_station_id`, `to_station_id`, `travel_class`, `booking_date`, `total_fare`, `booking_status`) VALUES
(1, '8245192837', 2, 1, 2, 1, '3A', '2026-11-18 10:30:00', 3850.00, 'CONFIRMED');

-- Passengers under Booking 1
INSERT INTO `booking_passengers` (`passenger_id`, `booking_id`, `full_name`, `age`, `gender`, `berth_preference`, `seat_id`, `passenger_status`, `fare`) VALUES
(1, 1, 'Rahul Sharma', 29, 'MALE', 'LOWER', 21, 'CONFIRMED', 1925.00), -- Seat 1 in Coach B1
(2, 1, 'Priya Patel', 27, 'FEMALE', 'MIDDLE', 22, 'CONFIRMED', 1925.00); -- Seat 2 in Coach B1

-- Payment for Booking 1
INSERT INTO `payments` (`payment_id`, `booking_id`, `transaction_reference`, `payment_method`, `amount`, `payment_status`, `payment_time`) VALUES
(1, 1, 'TXN_20261118_849201', 'UPI', 3850.00, 'SUCCESS', '2026-11-18 10:32:00');
