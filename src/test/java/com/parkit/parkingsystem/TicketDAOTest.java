package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

import java.util.Date;

public class TicketDAOTest {

    public static TicketDAO ticketDAO;

    @BeforeEach
    private void setUpPerTest() {
        ticketDAO = new TicketDAO();
    }


    @Test
    public void saveTicketCarTest ()  {
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date());
        ticket.setId(5);
        ticket.setVehicleRegNumber("2525");
        ParkingSpot parkingSpot = new ParkingSpot(2, ParkingType.CAR, false);
        ticket.setParkingSpot(parkingSpot);

        ticketDAO.saveTicket(ticket);

        Ticket result = ticketDAO.getTicket("2525");

        assertThat(result.getVehicleRegNumber()).isEqualTo(ticket.getVehicleRegNumber());
    }

    @Test
    public void saveTicketBikeTest ()  {
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date());
        ticket.setId(5);
        ticket.setVehicleRegNumber("1000");
        ParkingSpot parkingSpot = new ParkingSpot(5, ParkingType.BIKE, false);
        ticket.setParkingSpot(parkingSpot);

        ticketDAO.saveTicket(ticket);

        Ticket result = ticketDAO.getTicket("1000");

        assertThat(result.getVehicleRegNumber()).isEqualTo(ticket.getVehicleRegNumber());
    }

    @Test
    public void updateTicketTest ()  {
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date());
        ticket.setVehicleRegNumber("ABC");
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setParkingSpot(parkingSpot);

        ticketDAO.saveTicket(ticket);
        ticket.setOutTime(new Date());

        ticketDAO.updateTicket(ticket);

        Ticket updatedResult = ticketDAO.getTicket("ABC");
        assertThat(updatedResult.getVehicleRegNumber()).isEqualTo(ticket.getVehicleRegNumber());
    }

    @Test
    public void updateTicketWithUnknownVehiculeNumberTest () {
        Ticket ticket;
        ticket = ticketDAO.getTicket(null);
        boolean result = ticketDAO.updateTicket(ticket);

        assertThat(result).isEqualTo(false);
    }

    @Test
    public void saveTicketWithUnknownVehicleNumberTest() {
        Ticket ticket;
        ticket = ticketDAO.getTicket(null);
        boolean result = ticketDAO.saveTicket(ticket);

        assertThat(result).isEqualTo(false);
    }

}
