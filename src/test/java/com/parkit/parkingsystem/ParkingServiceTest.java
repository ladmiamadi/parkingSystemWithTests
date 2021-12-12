package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.*;
import java.util.Date;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @Mock
    private static DataBasePrepareService dataBasePrepareService;

    @BeforeEach
    private void setUpPerTest() {
        try {
            dataBasePrepareService.clearDataBaseEntries();
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processIncomingVehicleTestForCar () {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(2);
        parkingService.processIncomingVehicle();
        verify(ticketDAO, Mockito.times(1)).saveTicket(any());
    }

    @Test
    public void processIncomingVehicleTestForBike () {
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(4);

        parkingService.processIncomingVehicle();
        verify(ticketDAO, Mockito.times(1)).saveTicket(any());
    }

    @Test
    public void processExitingVehicleTest(){
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void processDiscountForRecurrentUsersTest () {
        parkingService.setRecurrentUser(true);
        parkingService.processExitingVehicle();

        double result = ticketDAO.getTicket(anyString()).getPrice();
        verify(ticketDAO, Mockito.times(1)).updateTicket(any());
        assertThat(result).isEqualTo(Fare.CAR_RATE_PER_HOUR * 5 / 100);
    }

    // Tester les cas limites
    @Test
    public void processExitingVehicleIfInvalidTicketTest(){
        when(ticketDAO.getTicket(any())).thenReturn(null);
        parkingService.processExitingVehicle();

        verify(ticketDAO, Mockito.never()).updateTicket(any());
    }

    @Test
    public void processIncomingVehicleWithNullParkingSpotTest() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenThrow(NullPointerException.class);
        parkingService.processIncomingVehicle();

        verify(ticketDAO, Mockito.never()).saveTicket(any());
    }
    @Test
    public void processIncomingVehicleWithInvalidTypeVehicleTest() {
        when(inputReaderUtil.readSelection()).thenThrow(IllegalArgumentException.class);

        verify(ticketDAO, Mockito.never()).saveTicket(any());
    }

    @Test
    public void TestGettingNextParkingNumberCarIfParkingIsFullShouldReturnNull () {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        ParkingSpot parkingSpot1 = new ParkingSpot(1, ParkingType.CAR, false);
        ParkingSpot parkingSpot2 = new ParkingSpot(2, ParkingType.CAR, false);
        ParkingSpot parkingSpot3 = new ParkingSpot(3, ParkingType.CAR, false);

        when(parkingSpotDAO.updateParking(parkingSpot1)).thenReturn(true);
        when(parkingSpotDAO.updateParking(parkingSpot2)).thenReturn(true);
        when(parkingSpotDAO.updateParking(parkingSpot3)).thenReturn(true);
        ParkingSpot parkingSpotResult = parkingService.getNextParkingNumberIfAvailable();

        assertThat(parkingSpotResult).isNull();
    }

    @Test
    public void TestGettingNextParkingNumberBikeIfParkingIsFullShouldReturnNull () {
        when(inputReaderUtil.readSelection()).thenReturn(2);
        ParkingSpot parkingSpot1 = new ParkingSpot(4, ParkingType.BIKE, false);
        ParkingSpot parkingSpot2 = new ParkingSpot(5, ParkingType.BIKE, false);
        when(parkingSpotDAO.updateParking(parkingSpot1)).thenReturn(true);
        when(parkingSpotDAO.updateParking(parkingSpot2)).thenReturn(true);

        ParkingSpot parkingSpotResult = parkingService.getNextParkingNumberIfAvailable();

        assertThat(parkingSpotResult).isNull();
    }
}