package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;


    @BeforeAll
    private static void setUp(){
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        Ticket ticketResult = ticketDAO.getTicket("ABCDEF");

        assertThat(ticketResult).isNotNull();
        assertThat(ticketResult.getParkingSpot().isAvailable()).isEqualTo(false);
    }

    @Test
    public void testParkingLotExit() throws InterruptedException {
        Ticket ticket = new Ticket ();
        Date intTime = new Date();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        intTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));

        ticket.setInTime(intTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setPrice(0);
        ticket.setVehicleRegNumber("ABCDEF");
        ticketDAO.saveTicket(ticket);
        Thread.sleep(600);
        parkingService.processExitingVehicle();
        Ticket ticketResult = ticketDAO.getTicket("ABCDEF");

        assertThat(ticketResult.getPrice()).isEqualTo(1.5);
        assertThat(ticketResult.getOutTime()).isAfterOrEqualTo(ticketResult.getInTime());
    }

    @Test
    public void testUpdatingParkingSpotAfterExit () throws InterruptedException {
        testParkingACar();
        testParkingLotExit();
        Ticket ticketResult = ticketDAO.getTicket("ABCDEF");

        int parkingSpotResult = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        assertThat(ticketResult.getParkingSpot().getId()).isEqualTo(parkingSpotResult);
    }
}
