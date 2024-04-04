package com.moveinsync.flightbooking.repository;

import com.moveinsync.flightbooking.model.Flight;
import com.moveinsync.flightbooking.model.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface FlightRepo extends JpaRepository<Flight,Integer> {
    public List<Flight> findAllByDateAndDepartureAirportEqualsIgnoreCaseAndArrivalAirportEqualsIgnoreCase(Date date, String source, String destination);
    @Query("SELECT DISTINCT f FROM Flight f JOIN f.seats s WHERE f.date = :date AND f.departureAirport = :departureAirport AND f.arrivalAirport = :arrivalAirport AND s.seatType = :seatType AND s.booked=false")
    public List<Flight> findByDateAndDepartureAirportAndArrivalAirportAndSeatType(
            @Param("date") Date date,
            @Param("departureAirport") String departureAirport,
            @Param("arrivalAirport") String arrivalAirport,
            @Param("seatType") SeatType seatType);

    public Flight findByFlightNumberIgnoreCase(String flightNumber);
    public void deleteByFlightNumberIgnoreCase(String flightNumber);

    Flight findById(Long flightId);

}
