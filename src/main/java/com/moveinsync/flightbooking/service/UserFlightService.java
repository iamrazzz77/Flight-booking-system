package com.moveinsync.flightbooking.service;
import com.moveinsync.flightbooking.model.Flight;
import com.moveinsync.flightbooking.model.SeatType;
import com.moveinsync.flightbooking.repository.FlightRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.util.List;

@Service
public class UserFlightService {

    @Autowired
    private FlightRepo flightRepo;

    public List<Flight> findFlight(Date date,String source,String destination){
        return flightRepo.findAllByDateAndDepartureAirportEqualsIgnoreCaseAndArrivalAirportEqualsIgnoreCase(date,source,destination);
    }

    public List<Flight> findFlightWithClass(Date date, String source, String destination, SeatType seatType){
        return flightRepo.findByDateAndDepartureAirportAndArrivalAirportAndSeatType(date, source, destination, seatType);
    }
}
