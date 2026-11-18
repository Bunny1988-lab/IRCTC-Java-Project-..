# Database Scripts & Migrations

This directory contains the relational database DDL and DML scripts for the **IRCTC Railway Reservation System**.

---

## Script Files

1. [`01_schema.sql`](file:///c:/Users/My%20PC/Documents/Java-IRCTC-project/database/01_schema.sql)
   - Creates the `irctc_db` database (InnoDB, `utf8mb4`).
   - Defines all 11 normalized tables with Primary Keys, Foreign Keys, Unique Keys, and Check constraints.
   - Sets up performance indexes for search, PNR lookup, and seat occupancy checks.
   - Safely drop-recreates tables in reverse-dependency order for repeatable execution.

2. [`02_seed_data.sql`](file:///c:/Users/My%20PC/Documents/Java-IRCTC-project/database/02_seed_data.sql)
   - Populates initial realistic sample records:
     - **Users**: 1 Admin (`admin`), 3 Passengers (`rahul_sharma`, `priya_patel`, `amit_verma`).
     - **Stations**: 10 major junction stations (NDLS, MMCT, BVI, ST, BRC, RTM, KOTA, CNB, HWH, MAS).
     - **Trains**: Mumbai Rajdhani (12951 / 12952), Bhopal Shatabdi (12002), Howrah Rajdhani (12301).
     - **Route Stops**: Sequential halting schedules with track distances and arrival/departure timings.
     - **Coaches & Seats**: Physical coach layouts (`1A`, `2A`, `3A`) and berth types (`LOWER`, `MIDDLE`, `UPPER`, `SIDE_LOWER`, `SIDE_UPPER`, `CABIN`).
     - **Schedules**: Calendar departure runs for operational testing.
     - **Sample Booking**: One historical confirmed PNR (`8245192837`) with 2 passengers and a completed simulated UPI payment.

---

## How to Execute the Scripts

### Method 1: Using MySQL Command Line Client

Open PowerShell or Command Prompt:

```bash
# 1. Execute schema creation (DDL)
mysql -u root -p < "c:\Users\My PC\Documents\Java-IRCTC-project\database\01_schema.sql"

# 2. Execute seed data insertion (DML)
mysql -u root -p < "c:\Users\My PC\Documents\Java-IRCTC-project\database\02_seed_data.sql"
```

Or from inside the MySQL shell:
```sql
SOURCE c:/Users/My PC/Documents/Java-IRCTC-project/database/01_schema.sql;
SOURCE c:/Users/My PC/Documents/Java-IRCTC-project/database/02_seed_data.sql;
```

### Method 2: Using MySQL Workbench / DBeaver

1. Open MySQL Workbench and connect to your local MySQL 8 instance.
2. Go to **File -> Open SQL Script...** and select `01_schema.sql`.
3. Click the **Execute (Lightning icon)** to run all statements.
4. Open `02_seed_data.sql` and execute it in the same manner.

---

## Verification Queries

To confirm the setup succeeded in MySQL:

```sql
USE irctc_db;

-- 1. Verify all 11 tables exist
SHOW TABLES;

-- 2. Verify master data counts
SELECT 'users' AS table_name, COUNT(*) AS count FROM users
UNION ALL
SELECT 'stations', COUNT(*) FROM stations
UNION ALL
SELECT 'trains', COUNT(*) FROM trains
UNION ALL
SELECT 'route_stops', COUNT(*) FROM route_stops
UNION ALL
SELECT 'coaches', COUNT(*) FROM coaches
UNION ALL
SELECT 'seats', COUNT(*) FROM seats
UNION ALL
SELECT 'train_schedules', COUNT(*) FROM train_schedules
UNION ALL
SELECT 'bookings', COUNT(*) FROM bookings
UNION ALL
SELECT 'booking_passengers', COUNT(*) FROM booking_passengers
UNION ALL
SELECT 'payments', COUNT(*) FROM payments;

-- 3. Verify route stops for Mumbai Rajdhani (12951)
SELECT rs.stop_sequence, s.station_code, s.station_name, rs.arrival_time, rs.departure_time, rs.distance_from_source_km
FROM route_stops rs
JOIN stations s ON rs.station_id = s.station_id
WHERE rs.train_id = 1
ORDER BY rs.stop_sequence;
```
