package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class ParkingSpotDAOTest {
    public static ParkingSpotDAO parkingSpotDAO;

    @BeforeEach
    private void setUpPerTest() {
        parkingSpotDAO = new ParkingSpotDAO();
    }

    @Test
    public void updateParkingCarTest () {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

        parkingSpotDAO.updateParking(parkingSpot);

        int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        assertThat(parkingSpot.getId()).isEqualTo(result);
    }

    @Test
    public void updateParkingBikeTest () {
        ParkingSpot parkingSpot = new ParkingSpot(4, ParkingType.BIKE, true);

        parkingSpotDAO.updateParking(parkingSpot);

        int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE);

        assertThat(parkingSpot.getId()).isEqualTo(result);
    }

    @Test
    public void updateInvalidParkingSpotCarTest () {
        boolean result = parkingSpotDAO.updateParking(null);

        assertThat(result).isEqualTo(false);
    }
}
