package com.irctc.dao;

import com.irctc.dao.impl.BookingDaoImpl;
import com.irctc.dao.impl.PaymentDaoImpl;
import com.irctc.dao.impl.RouteStopDaoImpl;
import com.irctc.dao.impl.SeatDaoImpl;
import com.irctc.dao.impl.StationDaoImpl;
import com.irctc.dao.impl.TrainDaoImpl;
import com.irctc.dao.impl.TrainScheduleDaoImpl;
import com.irctc.dao.impl.UserDaoImpl;
import com.irctc.database.DatabaseConnection;
import com.irctc.exception.DatabaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DaoInstantiationTest {

    @Test
    @DisplayName("All DAO implementations should instantiate successfully")
    void testDaoInstantiation() {
        UserDao userDao = new UserDaoImpl();
        StationDao stationDao = new StationDaoImpl();
        TrainDao trainDao = new TrainDaoImpl();
        RouteStopDao routeStopDao = new RouteStopDaoImpl();
        TrainScheduleDao scheduleDao = new TrainScheduleDaoImpl();
        SeatDao seatDao = new SeatDaoImpl();
        BookingDao bookingDao = new BookingDaoImpl();
        PaymentDao paymentDao = new PaymentDaoImpl();

        assertNotNull(userDao);
        assertNotNull(stationDao);
        assertNotNull(trainDao);
        assertNotNull(routeStopDao);
        assertNotNull(scheduleDao);
        assertNotNull(seatDao);
        assertNotNull(bookingDao);
        assertNotNull(paymentDao);
    }

    @Test
    @DisplayName("DAO operations should throw DatabaseException when unconfigured without crashing JVM")
    void testDaoFailureModeWhenUnconfigured() {
        if (!DatabaseConnection.isConfigured()) {
            UserDao userDao = new UserDaoImpl();
            assertThrows(DatabaseException.class, () -> userDao.findById(1L));

            StationDao stationDao = new StationDaoImpl();
            assertThrows(DatabaseException.class, stationDao::findAll);
        }
    }
}
