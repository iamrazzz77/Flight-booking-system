package com.moveinsync.flightbooking.service;

import com.moveinsync.flightbooking.dto.FlightDto;
import com.moveinsync.flightbooking.model.Flight;
import com.moveinsync.flightbooking.model.FlightSeat;
import com.moveinsync.flightbooking.model.FlightSeatClass;
import com.moveinsync.flightbooking.model.Reportmodel;
import com.moveinsync.flightbooking.repository.FlightRepo;
import com.moveinsync.flightbooking.repository.ReportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AdminService {
    @Autowired
    private FlightRepo flightRepo;

    @Autowired
    ReportRepo reportRepo;
    public boolean addFlight(Flight flight){
        try {
            Reportmodel newReport = new Reportmodel();
            newReport.setFlightNumber(flight.getFlightNumber());
            reportRepo.save(newReport);
            flightRepo.save(flight);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateFlight(FlightDto flightDto){
        try {
            String flightNumber = flightDto.getFlightNumber();
            Flight originalFlight = flightRepo.findByFlightNumberIgnoreCase(flightNumber);
            String departureAirport = flightDto.getDepartureAirport()==null? originalFlight.getDepartureAirport() :flightDto.getDepartureAirport();
            String arrivalAirport = flightDto.getArrivalAirport()==null?originalFlight.getArrivalAirport():flightDto.getArrivalAirport();
            LocalDateTime departureTime = flightDto.getDepartureTime()==null?originalFlight.getDepartureTime():flightDto.getDepartureTime();
            LocalDateTime arrivalTIme = flightDto.getArrivalTime()==null?originalFlight.getArrivalTime():flightDto.getArrivalTime();
            Date date = flightDto.getDate()==null?originalFlight.getDate():flightDto.getDate();
            Duration duration = flightDto.getFlightDuration()==null?originalFlight.getFlightDuration():flightDto.getFlightDuration();
            Double price = flightDto.getTicketPrice()==null?originalFlight.getTicketPrice():flightDto.getTicketPrice();
            Integer totalSeats = flightDto.getTotalSeats()==null?originalFlight.getTotalSeats():flightDto.getTotalSeats();
            String airline = flightDto.getAirlineName()==null?originalFlight.getAirlineName():flightDto.getAirlineName();
            String aircraft = flightDto.getAircraftType()==null?originalFlight.getAircraftType():flightDto.getAircraftType();
            String arrivalCity = flightDto.getArrivalCity()==null?originalFlight.getArrivalCity():flightDto.getArrivalCity();
            String departureCity = flightDto.getDepartureCity()==null?originalFlight.getDepartureCity():flightDto.getDepartureCity();
            String terminal= flightDto.getTerminal()==null?originalFlight.getTerminal():flightDto.getTerminal();
            String gateNo = flightDto.getGateNo()==null?originalFlight.getGateNo():flightDto.getGateNo();
            List<FlightSeatClass> flightSeatClasses = flightDto.getFlightSeatClasses()==null?originalFlight.getFlightSeatClasses():flightDto.getFlightSeatClasses();
            List<FlightSeat> seats = new ArrayList<>();
            while(flightRepo.findByFlightNumberIgnoreCase(flightNumber)!=null) {
                flightRepo.deleteByFlightNumberIgnoreCase(flightNumber);
            }
            Flight newFlight = new Flight(flightNumber,departureAirport,departureTime,date,arrivalAirport,arrivalTIme,duration,price,totalSeats,airline,aircraft,departureCity,arrivalCity,terminal,gateNo,flightSeatClasses,seats);
            flightRepo.save(newFlight);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public boolean deleteFlight(String flightNumber) {
        try {
            reportRepo.deleteByFlightNumberIgnoreCase(flightNumber);
            flightRepo.deleteByFlightNumberIgnoreCase(flightNumber);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}